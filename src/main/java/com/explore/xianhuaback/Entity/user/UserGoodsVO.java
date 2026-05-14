package com.explore.xianhuaback.Entity.user;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class UserGoodsVO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;

    private String imageUrl;

    private String productName;

    private String category;

    private Integer price;

    private Integer number;

    private Integer sales_count;

    private Integer status;

    private String createdTime;
}
