package com.explore.xianhuaback.Entity.AdminSales;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class SalesSummaryVO implements Serializable {

    private Integer totalSalesQuantity;

    private Integer totalComboQuantity;

    private Integer totalMarketingQuantity;

    private BigDecimal totalTurnoverAmount;
}
