package com.explore.xianhuaback.Entity.AdminPoints;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class AdminPoints implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;



    private String pointName; //积分的名称

    private Integer pointQuery;

    private Integer pointNumber;

    private String pointDescription;

    //状态的情况
    private Integer status;
}
