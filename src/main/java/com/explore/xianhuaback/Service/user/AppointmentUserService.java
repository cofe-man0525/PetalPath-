package com.explore.xianhuaback.Service.user;

import com.explore.xianhuaback.DTO.AppointmentUserDTO.AppointmentUserDTO;

public interface AppointmentUserService {

    //将对应的排队的情况进行写入中
    String joinWaitingQueue(AppointmentUserDTO appointmentUserDTO);

}
