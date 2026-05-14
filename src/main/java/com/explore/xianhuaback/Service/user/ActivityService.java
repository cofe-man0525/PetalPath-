package com.explore.xianhuaback.Service.user;

import com.explore.xianhuaback.Entity.ActivityUser.ActivityUserVO;
import com.explore.xianhuaback.Entity.ActivityUser.GetActivityUserVO;

import java.util.List;

public interface ActivityService {

    //获得对应活动的列表情况
    List<ActivityUserVO> getActivityList();

    //根据用户id来查询用户的数据情况
    List<GetActivityUserVO> getUserActivity(String userId);


    Boolean addUserActivity(String userId, String activityId);
}
