package com.explore.xianhuaback.Service.user;

import com.explore.xianhuaback.DTO.UserIdDTO;
import com.explore.xianhuaback.Entity.UserShopPoints.ShopUserPointsVO;
import com.explore.xianhuaback.Entity.UserShopPoints.UserPurchasePointsVO;

import java.util.List;

public interface ShopUserPointsService {


    List<ShopUserPointsVO> getShopPoints();  //进行数值得传递得情况

    //一次性的查询两张表的情况
    List<UserPurchasePointsVO> getByUsersPoints(String userId);

    //兑换失败的情况进行解决
    String  getRedeemPoints(UserIdDTO userIdDTO);

    //查询到数据库的优惠劵用户失效的情况进行修改
    String SelectStatusData(String userId);
}
