package com.explore.xianhuaback.Service.user;

import com.explore.xianhuaback.DTO.UserAIChatDTO.UserChatAIDTO;
import com.explore.xianhuaback.Entity.ChatAIRespones.ChatAiResponseVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


public interface ChatAIUserService {
    String ChatAiMessage(UserChatAIDTO userChatAIDTO);
}
