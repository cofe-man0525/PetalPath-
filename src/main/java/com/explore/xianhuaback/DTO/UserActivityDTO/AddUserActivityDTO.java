package com.explore.xianhuaback.DTO.UserActivityDTO;

import lombok.Data;

import java.io.Serializable;

@Data
public class AddUserActivityDTO implements Serializable {

    private String userId;

    private String activityId;
}
