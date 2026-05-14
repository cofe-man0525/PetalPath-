package com.explore.xianhuaback.Entity;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class SubmitAdmin implements Serializable {

    //序列化操作
    @Serial
    private static final long serialVersionUID = 1L;
    //用户名
    private String userName;

    //密码
    private String passWord;

    //邮箱
    private String email;

    //注册时间
    private LocalDateTime registerTime;

    //最新的更新时间
    private LocalDateTime LastLoginTime;

}
