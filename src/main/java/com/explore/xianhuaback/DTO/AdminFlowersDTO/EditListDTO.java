package com.explore.xianhuaback.DTO.AdminFlowersDTO;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
@Data
public class EditListDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Integer id;

    private String goodsName;

    private String goodsSort;

    private Double  goodsPrice;

    private Integer goodsNumber;


}
