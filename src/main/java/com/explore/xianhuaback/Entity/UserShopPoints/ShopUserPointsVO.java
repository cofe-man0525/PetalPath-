package com.explore.xianhuaback.Entity.UserShopPoints;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class ShopUserPointsVO implements Serializable {
    private Long id;

    /**
     * 图片地址（存储兑换商品或积分的展示图片URL）
     */
    private String imageUrl;

    /**
     * 积分名称（例如：兑换优惠券名称）
     */
    private String pointName;

    /**
     * 兑换所需积分数量（即消耗多少积分可兑换）
     */
    private Integer pointRequired;

    /**
     * 库存数量（该积分兑换项目的剩余可兑换次数）
     */
    private Integer pointNumber;

    /**
     * 积分详情（详细描述兑换内容或规则）
     */
    private String pointDescription;

    /**
     * 创建时间
     */

    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;

}
