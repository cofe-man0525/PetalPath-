package com.explore.xianhuaback.Entity;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class OrderPayMessage implements Serializable {

    private Long orderNo;              // 订单号
    private String userId;             // 用户ID
    private String addressId;              //地址id
    private String orderKey;           // 订单键值key
    private String remark;              //订单的备注
    private String createTime;         // 创建时间
    private Long goodsId;              // 单品ID
    private Integer goodsNumber;       // 单品数量
    private Long stockComboId;         // 套餐ID
    private Integer goodsComboNumber;  // 套餐数量
    private BigDecimal payAmount;   //订单的总金额
    private BigDecimal totalAmount;  //订单原价格
    private BigDecimal discountAmount;   //优惠价格

}
