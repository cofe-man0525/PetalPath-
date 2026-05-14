package com.explore.xianhuaback.DTO.UserLoginDTO;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserAddressEditDTO implements Serializable {

    private String id;

    private String userId;

    private String name;

    private String phone;

    private String city;

    private String detail;

    //是否为默认的数值
    private Integer isDefault;

    //创建时间
    private String createTime;

    //更新时间

    private String updateTime;

}
