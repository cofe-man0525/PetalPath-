package com.explore.xianhuaback.DTO.AdminActivityDTO;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class EditActivityImagesDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String folder;

    private Long id;

    private String url;
}
