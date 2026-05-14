package com.explore.xianhuaback.DTO;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;


@Data
public class AdminLoginDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    //用户id
    private Long id;
    //账号
    private String userName;

    //密码
    private String passWord;

}
