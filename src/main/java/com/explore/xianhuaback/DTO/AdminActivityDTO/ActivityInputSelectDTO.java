package com.explore.xianhuaback.DTO.AdminActivityDTO;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
@Data
public class ActivityInputSelectDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    //输入框的输入内容
    private String topInput;

    //展示的状态情况
    private Integer status;
}
