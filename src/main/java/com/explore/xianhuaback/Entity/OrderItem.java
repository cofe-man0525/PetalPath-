package com.explore.xianhuaback.Entity;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class OrderItem implements Serializable {


    private String orderNo;

    private Integer productId;

    private Long goodsId;

    private Integer comboId;

    private Integer quantity;

    private BigDecimal itemPrice;

    //主标题的
    private String itemName;

    //副标题
    private String itemNameTitle;

    private Integer type;




}
