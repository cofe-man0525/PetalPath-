package com.explore.xianhuaback.Entity.ActivityUser;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户参与活动实体类
 */
@Data
public class GetActivityUserVO implements Serializable {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 图片URL
     */
    private String imageUrl;

    /**
     * 活动的主题
     */
    private String activityTheme;

    /**
     * 活动的详情
     */
    private String activityDetail;

    /**
     * 活动的时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime activityTime;

    /**
     * 活动的类型：线上、线下、优惠促销
     */
    private String activityType;

    /**
     * 地点（线上可填"线上"或链接）
     */
    private String location;

    /**
     * 获得的积分数量
     */
    private Integer pointsObtained;

    /**
     * 个人的点击量
     */
    private Integer clickCount;

    /**
     * 报名的时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime signupTime;

    /**
     * 创建数据的时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
}