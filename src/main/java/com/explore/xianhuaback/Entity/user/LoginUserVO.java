package com.explore.xianhuaback.Entity.user;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class LoginUserVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;

    private String openId;

    private String phone;

    private String nickName;

    private String avatarUrl;

    private Integer totalPoints;

    private Integer totalOrderCount;

    private Integer status;

    private Integer isDeleted;

    private LocalDateTime createTime;

    //更新时间
    private LocalDateTime updateTime;
}
