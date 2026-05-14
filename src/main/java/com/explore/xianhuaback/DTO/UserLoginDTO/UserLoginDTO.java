package com.explore.xianhuaback.DTO.UserLoginDTO;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class UserLoginDTO implements Serializable {


    @Serial
    private static final long serialVersionUID = 1L;

    //临时授权码
    private String code;

    // 用户信息（新增）
    private String nickName;    // 微信昵称

    private String avatarUrl;   // 微信头像


}
