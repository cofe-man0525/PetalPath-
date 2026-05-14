package com.explore.xianhuaback.DTO.AdminFlowersDTO;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalTime;

@Data
public class AdminSettingDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String shopName;

    private String contactEmail;

    private String contactPhone;

    private String shopAddress;

    private String shopDescription;

    //是否营业
    private Integer isOpen;

    //Ai是否进行开启呢
    private Integer aiStart;

    private String startTime;  // 改为 String
    private String endTime;    // 改为 String

}
