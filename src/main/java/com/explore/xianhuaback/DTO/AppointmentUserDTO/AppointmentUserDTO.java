package com.explore.xianhuaback.DTO.AppointmentUserDTO;

import com.explore.xianhuaback.Service.user.AppointmentUserService;
import lombok.Data;

import java.io.Serializable;

@Data
public class AppointmentUserDTO implements Serializable {


    private String userId;

    private String name;

    private Integer phone;

    private String content;

    private String type;

    //对应的日期处理情况
    private String date;

}
