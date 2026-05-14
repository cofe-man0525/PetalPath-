package com.explore.xianhuaback.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class FlowersAddDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @JsonProperty("id")
    private Integer id;

    @JsonProperty("GoodsItem")
    private String GoodsItem;

    @JsonProperty("GoodsSort")
    private String GoodsSort;

    @JsonProperty("GoodsPrice")
    private String GoodsPrice;

    @JsonProperty("GoodsNumber")
    private Integer GoodsNumber;

    @JsonProperty("GoodsDescript")
    private String GoodsDescript;  // 注意：前端用的是 GoodsDescription



}
