package com.explore.xianhuaback.Entity.ActivityUser;

import lombok.Data;

import java.io.Serializable;

@Data
public class ActivityUser implements Serializable {
    private String userId;
    private String activityId;

    private String imageUrl;
    //主题活动
    private String themeName;

    //活动类型
    private Integer type;

    //活动状态
    private  Integer status;

    //活动详情
    private String description;

    //活动开始时间
    private String startTime;

    //活动结束时间
    private String endTime;

    //活动地址
    private String location;

    //活动最大人数
    private Integer maxAttendees;

    //创建的时间
    private String createdAt;
}
