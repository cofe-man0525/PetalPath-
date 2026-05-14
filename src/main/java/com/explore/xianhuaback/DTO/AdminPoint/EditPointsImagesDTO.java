package com.explore.xianhuaback.DTO.AdminPoint;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class EditPointsImagesDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String folder;

    private Long id;

    /** 七牛旧图完整 URL，删除后再上传新图 */
    private String url;
}
