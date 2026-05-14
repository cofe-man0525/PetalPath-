package com.explore.xianhuaback.Entity.AdminFlowers;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class FlowersGetData implements Serializable {

    //主名字
    private String productName;

    private String category;

    private BigDecimal price;
}
