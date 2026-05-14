package com.explore.xianhuaback.Entity.userIndex;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class UserIndexSignVO implements Serializable {


    @Serial
    private static final long serialVersionUID = 1L;

    private Integer id;

    private Integer userId;

    private String signDate;
    private String signTime;

    //获得积分数量
    private Integer points;

    //注册时间的情况
    private String createTime;




}
