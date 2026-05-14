package com.explore.xianhuaback.DTO.AdminPoint;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class AdminPointDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;

    private String pointName; //积分的名称

    private Integer pointQuery;

    private Integer pointNumber;

    private String pointDescription;

    private Integer status;

}
