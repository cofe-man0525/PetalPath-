package com.explore.xianhuaback.Entity.UserShopPoints;

import lombok.Data;

import java.io.Serializable;
@Data
public class TransactionPointsVO implements Serializable {

    private Long id;

    private Long userId;

    private Long adminPointId;

    private Integer status;

    private Integer expireDays;

    private String expireTime;

    private String validityEnd;

    private String createdAt;

    private String updatedAt;


}
