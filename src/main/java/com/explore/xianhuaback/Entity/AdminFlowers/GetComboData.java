package com.explore.xianhuaback.Entity.AdminFlowers;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class GetComboData implements Serializable {


    private String name;

    private String subtitle;

    private BigDecimal salePrice;
}
