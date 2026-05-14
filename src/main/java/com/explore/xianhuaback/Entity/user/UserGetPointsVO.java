package com.explore.xianhuaback.Entity.user;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserGetPointsVO implements Serializable {

    private Integer id;

    private Integer userId;

    private String signDate;

    private String signTime;

    private Integer points;

    private String createTime;

}
