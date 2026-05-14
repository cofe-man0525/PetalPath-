package com.explore.xianhuaback.Service.user.Impl;

import com.explore.xianhuaback.Config.RestTemplateConfig;
import com.explore.xianhuaback.DTO.UserAIChatDTO.UserChatAIDTO;
import com.explore.xianhuaback.Entity.ChatAIRespones.ChatAiResponseVO;
import com.explore.xianhuaback.Service.user.ChatAIUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class ChatAIUserServiceImpl implements ChatAIUserService {

    @Autowired
    private RestTemplateConfig restTemplateConfig;

    @Value("${ai.service.url}")
    private String aiServiceUrl;

    @Override
    public String ChatAiMessage(UserChatAIDTO userChatAIDTO) {

        // 构建请求 URL
        String url = aiServiceUrl + "/api/ai/chat";

        // 获取用户消息和用户ID
        String message = userChatAIDTO.getChatAIMessage();
        String userId = userChatAIDTO.getUserId();

        log.info("调用 Python AI 服务, URL: {}", url);
        log.info("用户ID: {}, 消息: {}", userId, message);

        // ✅ 修改点：字段名必须匹配 Python 的要求
        Map<String, Object> request = new HashMap<>();
        request.put("user_id", userId);    // 改为 user_id（下划线）
        request.put("question", message);  // 改为 question

        // 设置请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);

        // 发送请求
        Map response = restTemplateConfig.restTemplate().postForObject(url, entity, Map.class);

        log.info("Python 返回: {}", response);

        if (response != null && (Integer) response.get("code") == 200) {
            Map data = (Map) response.get("data");
            return (String) data.get("answer");
        }

        return "抱歉，服务暂时无法使用";
    }
}