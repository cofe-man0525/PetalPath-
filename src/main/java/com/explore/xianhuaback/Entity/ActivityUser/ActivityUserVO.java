package com.explore.xianhuaback.Entity.ActivityUser;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 活动信息实体类
 */
@Data
public class ActivityUserVO implements Serializable {

    private Integer id;

    private String imageUrl;


    private String themeName;


    private String description;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private String location;

    private Integer type;

    private Integer status;

    private Integer maxAttendees;

    private Long viewCount;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String updatedAt;
}