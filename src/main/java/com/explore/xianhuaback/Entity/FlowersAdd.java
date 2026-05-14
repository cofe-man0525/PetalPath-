package com.explore.xianhuaback.Entity;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class FlowersAdd implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    //商品名称
    private String GoodsItem;

    //商品分类
    private String GoodsSort;

    //商品价格
    private String GoodsPrice;

    //商品数量
    private Integer GoodsNumber;

    //销售数量
    private Integer SalesNumber;

    //描述
    private String GoodsDescript;

    //状态
    private Integer status;


}
