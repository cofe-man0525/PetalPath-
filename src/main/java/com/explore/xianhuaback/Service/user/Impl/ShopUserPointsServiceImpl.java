package com.explore.xianhuaback.Service.user.Impl;

import com.explore.xianhuaback.DTO.UserIdDTO;
import com.explore.xianhuaback.Entity.UserShopPoints.ShopUserPointsVO;
import com.explore.xianhuaback.Entity.UserShopPoints.TransactionPointsVO;
import com.explore.xianhuaback.Entity.UserShopPoints.UserPurchasePointsVO;
import com.explore.xianhuaback.Mapper.PointsMapper;
import com.explore.xianhuaback.Mapper.user.ShopUserPointsMapper;
import com.explore.xianhuaback.Service.user.ShopUserPointsService;
import com.explore.xianhuaback.Utils.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class ShopUserPointsServiceImpl implements ShopUserPointsService {

    //设置对应的
    private static final Integer LongTIme = 7;

    @Autowired
    //管理端的积分状况
    private static final String SHOP_USER_POINTS="shopUserPoints";

    //用户个人的数据用户情况
    private static final String SHOP_BY_USER_ID="shopByUserId:";
    @Autowired
    private ShopUserPointsMapper shopUserPointsMapper;

    //这里的是扣减库存的情况
    @Autowired
    private PointsMapper pointsMapper;


    @Autowired
    private RedisUtils redisUtils;
    //进行数值得传递得情况
    @Override
    public List<ShopUserPointsVO> getShopPoints() {
        log.info("进行数值传递得时候出现的情况");
        String key=SHOP_USER_POINTS;
        Object object= redisUtils.queryListWithSetCache(
               key,
               ShopUserPointsVO.class,
               ()->shopUserPointsMapper.getUserPoints());

       if(object==null){
           log.info("传递过来的数据是不存在的");
           throw new RuntimeException("传递过来的数据是不存在的");
       }

       //进行判断缓存过来的数据是什么类型的
        if(object instanceof String){
            List<ShopUserPointsVO> lists= Collections.singletonList((ShopUserPointsVO) object);
            return lists;
        }else{
            log.info("返回的其他的类型也是可以的");
            return  (List<ShopUserPointsVO>)  object;
        }
        //从这里的传递过来的数据情况
    }

    @Override
    public List<UserPurchasePointsVO> getByUsersPoints(String userId) {
        if (userId == null) {
            throw new RuntimeException("用户ID不能为空");
        }

        String key = SHOP_BY_USER_ID + userId;

        List<UserPurchasePointsVO> list = redisUtils.queryListWithSetCache(
                key,
                UserPurchasePointsVO.class,
                () -> shopUserPointsMapper.getUserPointsWithAdmin(userId)
        );

        return list == null ? Collections.emptyList() : list;
    }

    /**
     * 兑换积分卷的情况（即消耗多少积分可兑换）
     */
    @Override
    //这里的是针对于回滚事务如果存在多个数据库的表的设计的时候要添加上事务的情况
    @Transactional(rollbackFor = Exception.class)
    public String getRedeemPoints(UserIdDTO userIdDTO) {
        //这里进行将内容方法来进行解构出来数据和id的形式来
        String userId = userIdDTO.getUserId();

        String couponId=userIdDTO.getCouponId();

        //1.首选进行查询一下是否兑换过
        int count=shopUserPointsMapper.countByUserAndCoupon(userId,couponId);

        if(count>0){
            log.info("已经兑换过的情况");
            return "已经兑换过了";
        }

        //这里的是根据是扣减对应的商家的优惠劵的信息表
        int  updateCount=pointsMapper.decreaseStock(couponId);

        if(updateCount==0){
           log.info("库存已经进行扣减完毕");
           return "对应的积分兑换卷已经没有了";
        }

        //再次查询优惠劵的信息情况,是否存在这个信息
        TransactionPointsVO Vo=shopUserPointsMapper.getSelectById(couponId);

        //判断对应的情况中出现的情况
        if(Vo==null){
            //说明扣了库存但是没有出现这个数据的情况
            log.info("进行回滚的操作");
            pointsMapper.increaseStock(couponId);
            return "优惠劵不存在";
        }

        //记住这个是需要实体类的情况
        // ========== 第4步：创建兑换记录 ==========
        TransactionPointsVO record=new TransactionPointsVO();

        record.setUserId(Long.valueOf(userId));   //创建对应的用户id情况
        record.setAdminPointId(Long.valueOf(couponId)); //优惠劵的id
        record.setStatus(1); //设置对应的状态情况
        log.info("插入的情况进行设置过期时间");
        record.setExpireTime(String.valueOf(LocalDateTime.now().plusDays(7)));
        record.setValidityEnd(String.valueOf(LocalDateTime.now().plusDays(7)));

        log.info("设置对应的时间段数的情况");
        record.setExpireDays(LongTIme);
        //这里开始要进行向数据库插入数据了
        log.info("进行插入数据的情况，用来做解释的情况");
        //1.这里的是插入数据中要求的是针对于两张表的设计情况
        int countShopPoints= shopUserPointsMapper.insertShopPoints(record);

        //扣减对应的库存的情况将数据库的表数据库存进行减一

        int countAdmin=pointsMapper.decreaseStock(couponId);
        if(countShopPoints<=0){
            log.info("插入数据情况失败");
            throw new RuntimeException("插入数据失败");
        }

        if(countAdmin<=0){
            log.info("插入数据不存在，插入失败");
            throw new RuntimeException("插入失败");
        }

        log.info("兑换成功：userId={}, couponId={}", userId, couponId);
        return "插入成功";

    }


    //针对于用户的优惠劵是否进行失效呢
    @Override
    public String SelectStatusData(String userId) {
        if(userId==null){
            log.info("接收层出现的用户id不存在的");
            throw new RuntimeException("出现的用户id不存在的");
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        //将String类型的时间日期进行转换

        //进行将用户的数据情况
        List<TransactionPointsVO> lists =shopUserPointsMapper.getListPointsShop(userId);
        if(lists!=null || !lists.isEmpty()){
            log.info("这里的数据情况是存在的");

            lists.forEach((item)->{
                LocalDateTime validityEnd= LocalDateTime.parse(item.getValidityEnd(),formatter);  //这里的情况将日期进行转化
                LocalDateTime expireTime=  LocalDateTime.parse(item.getExpireTime(),formatter);

                if (expireTime.isBefore(LocalDateTime.now()) || validityEnd.isBefore(LocalDateTime.now())) {
                    log.info("这个优惠劵已经过期了");
                    //需要进行将状态改为正常的情况

                    int countStatus=shopUserPointsMapper.editDataStatus(item.getUserId());

                    if(countStatus<=0){
                        log.info("修改可能为失败的情况");
                        throw new RuntimeException("修改失败出现了错误情况");
                    }
                }
            });
        }

        return "无需进行修改状态的情况";

    }


}
