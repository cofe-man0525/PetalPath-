package com.explore.xianhuaback.Mapper.user;

import com.explore.xianhuaback.Entity.AppointmentUser.AppointmentUser;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AppointmentUserMapper {

    //根据对应的用户id和对应的日期来
    int countByIdMounth(String userId, String date);

    //将插入的数据对象到对应的数据表的情况
    int insertappointment(AppointmentUser appointmentUser);


    void updateDailyStats(String appointmentDate);


}
