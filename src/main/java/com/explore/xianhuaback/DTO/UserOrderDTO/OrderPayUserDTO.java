package com.explore.xianhuaback.DTO.UserOrderDTO;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class OrderPayUserDTO implements Serializable {

    //用户id
    private String userId;

    //雪花算法id对应的为订单编号
    private Long snowFlakeId;

    //购买的商品数量(单品数量)
    private Integer goodsNumber;

    //购买的套餐的数量
    private Integer goodsComboNumber;

    //优惠劵的id形式将前端的优惠劵进行传
    private String couponId;

    //商品规格id(单品)
    private String goodsId;

    //套餐的id
    private String  goodsComboId;

    //地址id
    private String addressId;

    //订单的备注消息
    private String remark;

    //实付金额
    private BigDecimal payAmount;

    //原来来的价格
    private BigDecimal totalAmount;

    //抵扣掉对应的优惠金额
    private BigDecimal discountAmount;







}
