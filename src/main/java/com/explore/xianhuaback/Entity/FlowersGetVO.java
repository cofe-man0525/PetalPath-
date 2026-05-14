package com.explore.xianhuaback.Entity;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

//VO为返回前端的数据类型
@Data
public class FlowersGetVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    //接收的对应返回的id
    private Long id;

    //商品名称
    private String GoodsName;

    //分类
    private String category;

    //商品价格
    private String GoodsPrice;

    //商品的数量
    private Integer GoodsNumber;

    //图片的地址
    private String imageUrl;

    //商品的销量
    private Integer SalesNumber;

    //状态
    private Integer  status;

    //创建时间
    private LocalDateTime createdTime;

}
