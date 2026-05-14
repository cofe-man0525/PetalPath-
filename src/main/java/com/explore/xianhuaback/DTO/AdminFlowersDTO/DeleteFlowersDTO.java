package com.explore.xianhuaback.DTO.AdminFlowersDTO;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class DeleteFlowersDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    //获得的id形式
    private Long id;
    //获得对应的Url地址
    private String imageUrl;
}
