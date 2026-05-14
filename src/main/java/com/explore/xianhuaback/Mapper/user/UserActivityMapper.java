package com.explore.xianhuaback.Mapper.user;


import com.explore.xianhuaback.Entity.ActivityUser.ActivityUser;
import com.explore.xianhuaback.Entity.ActivityUser.ActivityUserVO;
import com.explore.xianhuaback.Entity.ActivityUser.GetActivityUserVO;
import com.explore.xianhuaback.Entity.AdminAddActivity.ActivityVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UserActivityMapper {

    @Select("select * from activity  ")
    List<ActivityUserVO> getActivity();

    @Select("select * from user_activity where user_id=#{userId}")
    List<GetActivityUserVO> getUserActivity(String userId);

    @Select("select * from activity where id=#{activityId} ")
    ActivityVO getByActivityId(String activityId);

    Boolean insertUserActivity(ActivityUser activityUser);
}
