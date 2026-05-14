package com.explore.xianhuaback.Entity.user;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class UserMessageVO implements Serializable {

    //数据传输的情况对象
    @Serial
    private static final long serialVersionUID = 1L;

    //传递过来的是用户的id情况来展示好对应用户的数据情况
    private Long id;

    //用户id
//    对应的是对应的id类型管理
    private Long userId;

    //总积分
    private Integer totalPoints;

    //订单总数
    private Integer totalOrders;

    //优惠卷总数
    private Integer totalCoupons;

    //积分总数
    private Integer totalAppointments;

    //活动总数
    private Integer totalActivities;

    //购物车总数
    private Integer cartItemsCount;

    //消息总数
    private Integer totalMessages;

    //活动的总访问数量
    private Integer totalVisitsActivities;


}
