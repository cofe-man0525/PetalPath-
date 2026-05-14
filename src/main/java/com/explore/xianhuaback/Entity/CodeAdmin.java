package com.explore.xianhuaback.Entity;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class CodeAdmin implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String userName;
}
