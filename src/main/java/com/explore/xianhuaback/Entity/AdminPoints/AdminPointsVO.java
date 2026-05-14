package com.explore.xianhuaback.Entity.AdminPoints;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
@Data
public class AdminPointsVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Integer id;

    private String imageUrl;

    private String pointName; //积分的名称

    private Integer pointQuery;

    private Integer pointNumber;

    private Integer status; //积分的状态情况

    private String pointDescription;

    private String  createdAt;

    private String  updatedAt;
}
