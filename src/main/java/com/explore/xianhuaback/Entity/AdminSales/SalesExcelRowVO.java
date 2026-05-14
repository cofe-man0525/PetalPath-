package com.explore.xianhuaback.Entity.AdminSales;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class SalesExcelRowVO implements Serializable {

    private Long goodsId;

    private Integer comboId;

    private Integer itemType;

    private String itemName;

    private String itemNameTitle;

    private BigDecimal unitPrice;

    private Integer salesQuantity;

    private Integer comboQuantity;

    private Integer totalMarketingQuantity;

    private BigDecimal turnoverAmount;
}
