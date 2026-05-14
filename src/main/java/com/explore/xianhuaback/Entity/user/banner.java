package com.explore.xianhuaback.Entity.user;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
@Data
public class banner implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    //id
    private Long id;

    //图片
    private String imageUrl;

    //更新时间
    private String updatedAt;

    //注册时间
    private String createdAt;
}
