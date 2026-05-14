package com.explore.xianhuaback.Service.user.Impl;

import com.explore.xianhuaback.DTO.UserOrderDTO.OrderPayUserDTO;
import com.explore.xianhuaback.Entity.OrderPayMessage;
import com.explore.xianhuaback.Mapper.user.OrderPayUserMapper;
import com.explore.xianhuaback.Service.user.OrderPayUserService;
import com.explore.xianhuaback.Utils.StockLuaScript;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RSet;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class OrderPayUserServiceImpl implements OrderPayUserService {

    //商品单个的键
    private  static final String STOCK_GOODS_KEY="stock_goods_key";

    //商品套餐的键
    private static final String STOCK_COMBO_KEY="stock_combo_key";

    //队列名字分布情况
    private static final String QUEUE_ORDER="queue_order:";
    //对应的消息队列
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private OrderPayUserMapper orderPayUserMapper;

    //生产者的队列
    //创建对应的加入消息队列的情况展示
    @Override
    public String jointOrderCreate(OrderPayUserDTO orderPayUserDTO) {
       log.info("开始对应的消息传递给消息队列");

        Long orderNo=orderPayUserDTO.getSnowFlakeId();   //订单编号
        String userId=orderPayUserDTO.getUserId();   //用户id

        //购买的商品数量
        Integer goodsNumber=orderPayUserDTO.getGoodsNumber();
        //套餐的数量情况
        Integer goodsComboNumber=orderPayUserDTO.getGoodsComboNumber();

        String addressId=orderPayUserDTO.getAddressId();
        //单品的商品id
        String goodsId=orderPayUserDTO.getGoodsId();

        //套餐的id
        String stockComboId=orderPayUserDTO.getGoodsComboId();

        //分部的操作情况
        log.info("开始进行分步的判断的情况");
        if(goodsNumber>0 && goodsNumber!=null){
            String stockKey=STOCK_GOODS_KEY+goodsId;   //确保对应的数据是存在的
            DefaultRedisScript<Long> script = new DefaultRedisScript<>();
            script.setScriptText(StockLuaScript.DEDUCE_STOCK_LUA);   //这里存在的预先进行扣除对应的库存的情况
            script.setResultType(Long.class);

            Long result = (Long) redisTemplate.execute(script,
                    Collections.singletonList(stockKey),
                    String.valueOf(goodsNumber));

            if(result==null||result==0){

                log.info("影响对应的行数出现了错误");
                return "库存不足";
            }
        }

        log.info("开始进行的是套餐的数量判断");
        if(goodsComboNumber>0 && goodsComboNumber!=null){
            String stockKey=STOCK_COMBO_KEY+stockComboId;   //确保对应的数据是存在的
            DefaultRedisScript<Long> script = new DefaultRedisScript<>();
            script.setScriptText(StockLuaScript.DEDUCE_PACKAGE_STOCK_LUA);   //这里存在的预先进行扣除对应的库存的情况
            script.setResultType(Long.class);

            Long result = (Long) redisTemplate.execute(script,
                    Collections.singletonList(stockKey),
                    String.valueOf(goodsComboNumber));
            if(result==null || result==0){
                log.info("对应套餐的库存不足");
                return "套餐库存不足";
            }
        }


        //全局流量控制规定对应的等待支付的队列的人数最大的数量情况
        String globalKey = "payment:global:waiting";
        RSet<String> globalSet = redissonClient.getSet(globalKey);
        if(globalSet.size()>30){
            log.info("支付人群入队过多，暂停加入");
            return "请稍后重试";
        }

        //原子操作（用户级别的成功或者失败）
        //将订单进行加入对应的订单队列中
        String orderKey=QUEUE_ORDER+userId;
        RSet<String> waitingOrders=redissonClient.getSet(orderKey);
        log.info("进行判断对应的是否已经加入到对应的消息队列情况");
        if(waitingOrders.contains(orderKey)){
            return "已经加入到对应的order队列，准备进行支付";
        }


        log.info("将设置对应的自动销毁程序和程度");
        //设置过期时间防止对应的线程进行阻塞的情况
        waitingOrders.expire(Duration.ofHours(1));
        globalSet.add(userId);   //展示对应的情况展示
        globalSet.expire(Duration.ofHours(1));


        //构造支付的消息体，
        OrderPayMessage payMessage = new OrderPayMessage();
        payMessage.setOrderNo(orderNo);                             //订单编号
        payMessage.setUserId(userId);                               //用户id
        payMessage.setOrderKey(orderKey);                           //订单的键
        payMessage.setCreateTime(LocalDateTime.now().toString());   //商品的创建时间
        payMessage.setGoodsId(Long.valueOf(goodsId));               //单品商品id
        payMessage.setGoodsNumber(goodsNumber);                     //单品数量
        payMessage.setStockComboId(Long.valueOf(stockComboId));     //套餐商品的id
        payMessage.setGoodsComboNumber(goodsComboNumber);           //套餐的数量
        payMessage.setAddressId(addressId);                         //地址id
        payMessage.setPayAmount(orderPayUserDTO.getPayAmount());    //实付金额数量
        payMessage.setRemark(orderPayUserDTO.getRemark());          //订单的备注
        payMessage.setTotalAmount(orderPayUserDTO.getTotalAmount());  //订单的原价
        payMessage.setDiscountAmount(orderPayUserDTO.getDiscountAmount());  //订单的优惠价格
        //将支付的消息体传递给消息队列中（普通的队列中）
        rabbitTemplate.convertAndSend(
                "payment.exchange",      // 支付交换机
                "payment.process",       // 路由键（指定的对应的队列）
                payMessage               // 消息内容
        );


        //针对于对应延迟消息的确定的情况
        log.info("针对于对应的延迟消息队列情况");
        // 5. 发送延迟消息（30分钟后检查订单状态）
        rabbitTemplate.convertAndSend(
                "order.exchange",        // 延迟交换机（x-delayed-message类型）
                "order.delay",           // 路由键
                orderNo.toString(),      // 消息体（订单号）
                message -> {
                    // 设置30分钟延迟（毫秒）
                    message.getMessageProperties().setDelayLong((long) (30 * 60 * 1000));
                    return message;
                }
        );

        log.info("订单已经提交");
        return "订单已经提交";
    }

    //扣减失败的时候进行回滚单品数量的操作
    private void rollbackGoodsStock(Long skuId, Integer quantity){
        log.info("扣减对应的单品的数量");
        String stockKey = STOCK_GOODS_KEY + skuId;
        redisTemplate.opsForValue().increment(stockKey, quantity);
    }


    //扣减对应的套餐的数量操作
    private void rollbackComboStock(Long skuId, Integer quantity){

        log.info("扣减对应的库存数量");

        String stockGoodsKey=STOCK_COMBO_KEY+skuId;
        redisTemplate.opsForValue().increment(stockGoodsKey, quantity);
    }
}
