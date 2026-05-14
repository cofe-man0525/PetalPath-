package com.explore.xianhuaback.Entity.user;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class getUserAddressVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String id;
    private String userId;

    private String name;

    private String phone;

    private String city;

    private String detailAddress;

    private Integer isDefault;

    private String createTime;
    private String updateTime;



}
