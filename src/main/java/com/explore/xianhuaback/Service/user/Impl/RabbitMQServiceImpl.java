package com.explore.xianhuaback.Service.user.Impl;

import com.explore.xianhuaback.Config.RedisConfig;
import com.explore.xianhuaback.DTO.AppointmentUserDTO.AppointmentUserDTO;
import com.explore.xianhuaback.DTO.UserOrderDTO.OrderPayUserDTO;
import com.explore.xianhuaback.Entity.AdminFlowers.FlowersGetData;
import com.explore.xianhuaback.Entity.AdminFlowers.GetComboData;
import com.explore.xianhuaback.Entity.AppointmentUser.AppointmentUser;
import com.explore.xianhuaback.Entity.Order;
import com.explore.xianhuaback.Entity.OrderItem;
import com.explore.xianhuaback.Entity.OrderPayMessage;
import com.explore.xianhuaback.Entity.OrderVO;
import com.explore.xianhuaback.Mapper.FlowersMapper;
import com.explore.xianhuaback.Mapper.user.AppointmentUserMapper;
import com.explore.xianhuaback.Mapper.user.OrderPayUserMapper;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RSet;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class RabbitMQServiceImpl {

    private static final String APPOINTMENT_CAPACITY = "appointment:capacity:";

    @Autowired
    private AppointmentUserMapper appointmentUserMapper;

    @Autowired
    private FlowersMapper flowersMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private OrderPayUserMapper orderPayUserMapper;

    @Transactional
    public String createAppointment(AppointmentUserDTO appointmentUserDTO) {
        log.info("开始真实的业务场景");
        String date = appointmentUserDTO.getDate();
        String userId = appointmentUserDTO.getUserId();

        // 1. 检查用户的次数情况
        int countMount = appointmentUserMapper.countByIdMounth(userId, date);

        if (countMount >= 4) {
            log.info("达到对应的预约上限了");
            // 业务失败，回滚 Redis 库存
            rollbackCapacity(date);
            return "已经到达预约的上限了";
        }

        // 2. 创建预约对象
        AppointmentUser appointmentUser = new AppointmentUser();
        appointmentUser.setUserId(userId);
        appointmentUser.setName(appointmentUserDTO.getName());
        appointmentUser.setPhone(appointmentUserDTO.getPhone());
        appointmentUser.setCreateTime(String.valueOf(LocalDateTime.now()));
        appointmentUser.setAppointmentType(appointmentUserDTO.getType());
        appointmentUser.setAppointmentDate(appointmentUserDTO.getDate());
        appointmentUser.setAppointmentTime(appointmentUserDTO.getDate());
        appointmentUser.setRemark(appointmentUserDTO.getContent());

        // 3. 插入预约记录（返回影响行数）
        int result = appointmentUserMapper.insertappointment(appointmentUser);

        // 4. 插入成功，更新统计表
        if (result > 0) {
            log.info("插入成功，更新统计表");
            appointmentUserMapper.updateDailyStats(appointmentUser.getAppointmentDate());

            //这个话也要进行修改对应的版本号码
            log.info("预约成功");
            return "预约成功";
        } else {
            // 插入失败，回滚 Redis 库存
            log.error("插入预约记录失败");
            rollbackCapacity(date);
            return "预约失败，请稍后重试";
        }
    }

    /**
     * 回滚 Redis 库存
     */
    private void rollbackCapacity(String date) {
        String capacityKey = APPOINTMENT_CAPACITY + date;
        redisTemplate.opsForValue().decrement(capacityKey);
        log.warn("回滚库存：date={}", date);
    }

    private long getSecondsUntilEndOfDay() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime endOfDay = now.toLocalDate().atTime(23, 59, 59);
        return Duration.between(now, endOfDay).getSeconds();
    }

    //订单的支付情况
    @Transactional
    public String resultOrderPay(OrderPayMessage orderPayMessage) {

        log.info("OrderPayUserDTO已经传入扣减库存和进行支付的情况");

        Long orderNo = orderPayMessage.getOrderNo();   //订单的编号
        String userId = orderPayMessage.getUserId();   //用户id
        String orderKey = (String) orderPayMessage.getOrderKey();
        String goodsId = String.valueOf(orderPayMessage.getGoodsId());  //单个商品的id
        String comboId = String.valueOf(orderPayMessage.getStockComboId());   //套餐的id数据情况
        Integer goodsNumber = orderPayMessage.getGoodsNumber();  //单个商品的数量情况
        Integer goodsComboNumber = orderPayMessage.getGoodsComboNumber();   //套餐的对应的商品数量

        //获取到分布式锁的问题解决对应的分布式的解决方案
        String lockKey = "lock:order:user:" + userId;
        RLock lock = redissonClient.getLock(lockKey);

        try {
            log.info("看门狗机制进行执行中查到对应的业务是否执行完");
            if (!lock.tryLock(3, TimeUnit.SECONDS)) {
                log.warn("获取锁失败，用户：{}", userId);
                return "系统繁忙，请稍后重试";
            }

            log.info("拿到对应的锁机制开始处理对应的订单");
            OrderVO orderVO = orderPayUserMapper.getByOrderId(orderNo);

            if (orderVO != null) {
                log.info("订单已经存在了");
                clearQueueMark(orderKey, userId);
                return "订单已经创建了";
            }

            if (orderPayMessage.getGoodsId() != null) {
                log.info("存在这个");
            }

            //创建对应的订单插入到订单中
            //主要的记录表
            Order order = new Order();
            order.setOrderNo(String.valueOf(orderNo));   //订单号
            order.setAddressId(Long.valueOf(orderPayMessage.getAddressId()));  //订单的地址id
            order.setRemark(orderPayMessage.getRemark());   //订单的备注
            order.setCreateTime(LocalDateTime.now());   //表数据的创建时间
            order.setPayTime(LocalDateTime.now());  //订单的创建的时间
            order.setAddressId(Long.valueOf(orderPayMessage.getAddressId()));   //地址的id的格式
            order.setCouponId(Long.valueOf(orderPayMessage.getGoodsId())); //是否有优惠劵Id
            order.setDiscountAmount(orderPayMessage.getDiscountAmount());    //优惠的价格
            order.setPayAmount(orderPayMessage.getPayAmount());    //实际支付的价格
            order.setPayStatus(0);   //支付状态默认为0待支付的情况
            order.setTotalAmount(orderPayMessage.getTotalAmount());   //支付的原总价格
            order.setUserId(Long.valueOf(userId));   //用户的id

            int rows = orderPayUserMapper.insertOrder(order);
            if (rows <= 0) {
                log.error("主订单插入失败");
                clearQueueMark(orderKey, userId);
                return "订单创建失败";
            }
            log.info("主订单插入成功");

            //单个商品的表插入
            if (goodsId != null && goodsNumber != null && goodsNumber > 0) {
                OrderItem orderItem = new OrderItem();
                FlowersGetData flowersGetData = flowersMapper.getByIdFlowersGoods(comboId);
                orderItem.setOrderNo(String.valueOf(orderNo));   //订单号码
                orderItem.setItemName(flowersGetData.getProductName());
                orderItem.setType(1);
                orderItem.setOrderNo(String.valueOf(orderNo));
                orderItem.setComboId(Integer.valueOf(comboId));
                orderItem.setItemPrice(flowersGetData.getPrice());
                orderItem.setQuantity(goodsNumber);
                orderItem.setItemNameTitle(flowersGetData.getCategory());
                orderItem.setGoodsId(Long.valueOf(goodsId));
                orderPayUserMapper.insertGoods(orderItem);   //插入对应的数据库操作
            }

            //套餐表的插入情况
            if (comboId != null && goodsComboNumber != null && goodsComboNumber > 0) {
                OrderItem orderItem = new OrderItem();
                GetComboData getComboData = flowersMapper.getByIdCombo(goodsId);
                orderItem.setOrderNo(String.valueOf(orderNo));   //订单号码
                orderItem.setItemName(getComboData.getName());
                orderItem.setType(2);
                orderItem.setOrderNo(String.valueOf(orderNo));
                orderItem.setComboId(Integer.valueOf(comboId));
                orderItem.setItemPrice(getComboData.getSalePrice());
                orderItem.setQuantity(goodsComboNumber);
                orderItem.setItemNameTitle(getComboData.getSubtitle());
                orderItem.setGoodsId(Long.valueOf(goodsId));
                orderPayUserMapper.insertComboId(orderItem);   //插入对应的数据库操作
            }

            // 全部执行成功，清除排队标记并返回成功
            clearQueueMark(orderKey, userId);
            return "订单创建成功";

        } catch (Exception e) {
            log.error("订单创建失败", e);
            // 异常时也要清理标记
            clearQueueMark(orderKey, userId);
            // 回滚已扣减的库存
            rollbackStock(goodsId, goodsNumber, comboId, goodsComboNumber);
            return "订单创建失败：" + e.getMessage();
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    private void clearQueueMark(String orderKey, String userId) {
        // 清除用户队列标记
        RSet<String> waitingOrders = redissonClient.getSet(orderKey);
        waitingOrders.remove(orderKey);

        // 清除全局排队标记
        String globalKey = "payment:global:waiting";
        RSet<String> globalSet = redissonClient.getSet(globalKey);
        globalSet.remove(userId);
    }

    /**
     * 回滚库存（新增方法，供异常时调用）
     */
    private void rollbackStock(String goodsId, Integer goodsNumber, String comboId, Integer goodsComboNumber) {
        // 这里请根据你的实际库存回滚逻辑实现
        log.warn("回滚库存：goodsId={}, goodsNumber={}, comboId={}, goodsComboNumber={}",
                goodsId, goodsNumber, comboId, goodsComboNumber);
        // 示例：如果使用 Redis 扣减，这里需要 increment 回滚
        // 具体实现由你补充
    }
}