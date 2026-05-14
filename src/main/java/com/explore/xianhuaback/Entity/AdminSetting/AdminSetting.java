package com.explore.xianhuaback.Entity.AdminSetting;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class AdminSetting implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String shopName;

    private String contactEmail;

    private String contactPhone;

    private String shopAddress;

    private String shopDescription;

    //是否营业
    private Integer isOpen;

    private Integer aiStart;

    private String startTime;  // 改为 String
    private String endTime;    // 改为 String

    //注册时间
    private LocalDateTime createdAt;

    //更新时间
    private LocalDateTime updatedAt;

}
