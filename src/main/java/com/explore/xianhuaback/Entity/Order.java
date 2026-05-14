package com.explore.xianhuaback.Entity;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Order implements Serializable {

    private String orderNo;               // 订单号
    private Long userId;                  // 用户ID
    private Long addressId;               // 地址ID
    private Long couponId;                // 优惠券ID
    private String remark;                // 订单备注
    private BigDecimal totalAmount;       // 订单原总金额
    private BigDecimal discountAmount;    // 优惠抵扣金额
    private BigDecimal payAmount;         // 实付金额
    private Integer payStatus;            // 0-待支付 1-已支付 2-支付失败 3-已退款
    private LocalDateTime payTime;        // 支付时间
    private LocalDateTime createTime;     // 创建时间
}
