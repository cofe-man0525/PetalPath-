package com.explore.xianhuaback.Entity.user;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class WxLoginOpenId implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;


    private String appId;

    private String appSecret;
}
