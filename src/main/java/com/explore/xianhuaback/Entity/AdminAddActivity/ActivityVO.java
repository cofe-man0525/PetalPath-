package com.explore.xianhuaback.Entity.AdminAddActivity;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
//用来获取到对应的数据返回
@Data
public class ActivityVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String imageUrl;


    //传递对应的id形式
    private Long id;

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
