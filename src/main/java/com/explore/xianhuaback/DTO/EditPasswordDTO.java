package com.explore.xianhuaback.DTO;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class EditPasswordDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String userName;

    private String passWord;

    private Integer code;
}
