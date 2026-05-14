package com.explore.xianhuaback.Entity;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class AdminLogin implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    //用户id
    private Long id;
    //账号
    private String userName;

    //密码
    private String passWord;

    //注册时间
    private LocalDateTime registerTime;

    //最新的更新时间
    private LocalDateTime LastLoginTime;

}
