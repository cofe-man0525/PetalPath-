package com.explore.xianhuaback.Entity.AppointmentUser;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class AppointmentUser implements Serializable {

    private String userId;

    private String appointmentType;

    private String appointmentDate;

    private String appointmentTime;

    private String createTime;

    private String name;   //姓名

    private String remark;  //备注

    private Integer phone;  //手机


}
