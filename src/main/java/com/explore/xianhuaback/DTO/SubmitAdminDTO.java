package com.explore.xianhuaback.DTO;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

//设置的对应的参数形式
@Data
public class SubmitAdminDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    //用户id
    private Long id;
    //用户名
    private String userName;

    //密码
    private String passWord;

    //邮箱
    private String email;
}
