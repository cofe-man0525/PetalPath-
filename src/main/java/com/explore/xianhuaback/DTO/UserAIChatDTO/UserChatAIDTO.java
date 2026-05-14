package com.explore.xianhuaback.DTO.UserAIChatDTO;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserChatAIDTO implements Serializable {


    private String ChatAIMessage;

    private String userId;
}
