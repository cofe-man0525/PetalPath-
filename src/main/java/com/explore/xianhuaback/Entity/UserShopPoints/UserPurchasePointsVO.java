package com.explore.xianhuaback.Entity.UserShopPoints;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

//用户自己的选择情况
@Data
public class UserPurchasePointsVO  implements Serializable {

    // 来自 user_purchase_points 表
    private Long id;
    private Long userId;
    private Long adminPointsId;
    private Integer status;
    private Integer expireDays;
    private Date expireTime;
    private Date validityEnd;
    private Date createdAt;

    // 来自 adminPoints 表
    private String imageUrl;
    private String pointName;
    private Integer pointRequired;
    private String pointDescription;
}
