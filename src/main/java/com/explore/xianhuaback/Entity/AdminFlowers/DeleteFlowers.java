package com.explore.xianhuaback.Entity.AdminFlowers;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class DeleteFlowers implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private Long id;
    private String imageUrl;
}
