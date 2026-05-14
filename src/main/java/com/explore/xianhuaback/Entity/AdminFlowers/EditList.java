package com.explore.xianhuaback.Entity.AdminFlowers;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class EditList implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private Integer id;

    private String goodsName;

    private String goodsSort;

    private Double  goodsPrice;

    private Integer goodsNumber;
    //商品的销量
    private Integer SalesNumber;

    //状态
    private Integer status;

    private LocalDateTime createdTime;
}
