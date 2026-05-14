package com.explore.xianhuaback.Mapper.user;

import com.explore.xianhuaback.Entity.UserShopPoints.ShopUserPointsVO;
import com.explore.xianhuaback.Entity.UserShopPoints.TransactionPointsVO;
import com.explore.xianhuaback.Entity.UserShopPoints.UserPurchasePointsVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface ShopUserPointsMapper {

    //返回所有的数据情况
    @Select("select *  from  adminPoints")
    List<ShopUserPointsVO> getUserPoints();

    //一次性的返回两张表的情况
    List<UserPurchasePointsVO> getUserPointsWithAdmin(String userId);


    //这里进行的是将数据库的数据进行返回查询的情况
    @Select("select count(*) from user_purchase_points where user_id=#{userId} and admin_points_id=#{couponId}")
    int countByUserAndCoupon(String userId, String couponId);


    //再次查询优惠劵的信息
    @Select("select * from user_purchase_points where admin_points_id=#{couponId}")
    TransactionPointsVO getSelectById(String couponId);

    //同时操作两张表用来插入数据库
    int insertShopPoints(TransactionPointsVO record);

    @Select("select * from user_purchase_points where user_id=#{userId}")
    List<TransactionPointsVO> getListPointsShop(String userId);

    //这里设计的是对应的
    @Update("update user_purchase_points set status=1 where user_id=#{userId}")
    int editDataStatus(Long userId);
}
