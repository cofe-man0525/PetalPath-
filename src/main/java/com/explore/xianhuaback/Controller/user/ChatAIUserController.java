package com.explore.xianhuaback.Controller.user;

import com.explore.xianhuaback.DTO.UserAIChatDTO.UserChatAIDTO;
import com.explore.xianhuaback.Entity.ChatAIRespones.ChatAiResponseVO;
import com.explore.xianhuaback.Result.Result;
import com.explore.xianhuaback.Service.user.ChatAIUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/user")
public class ChatAIUserController {

    @Autowired
    private ChatAIUserService chatAIUserService;

    @PostMapping("/ChatAI")
    public Result<ChatAiResponseVO> ChatUserAIMessage(@RequestBody UserChatAIDTO userChatAIDTO){
        log.info("userChatAIDTO"+userChatAIDTO.getChatAIMessage());
        if(userChatAIDTO.getChatAIMessage()==null){
            log.info("AI这里的功能没有进行实现");
            throw new RuntimeException("前端传递的数据不存在的");
        }
        if(userChatAIDTO.getUserId()==null){
            log.info("传递得用户id不存在");
            throw new RuntimeException("传递过来得用户id不存在");
        }
        log.info("这里进行传递的是数据的接收");

        String messageList=chatAIUserService.ChatAiMessage(userChatAIDTO);
        log.info("已经生成了对应的提示问题和反应的问题正在进行返回");

        //生成对象实列进行返回数据情况
        ChatAiResponseVO chatAiResponseVO=new ChatAiResponseVO();
        chatAiResponseVO.setResponseMessage(messageList);
        return Result.success(chatAiResponseVO);
    }
}
