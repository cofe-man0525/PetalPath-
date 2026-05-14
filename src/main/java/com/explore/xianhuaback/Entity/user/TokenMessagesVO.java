package com.explore.xianhuaback.Entity.user;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
@Data
public class TokenMessagesVO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    //会话凭证
    private String token;

    //会话openid
    private String openid;

    //微信名
    private String nickname;

    //微信头像
    private String avatarUrl;

}
