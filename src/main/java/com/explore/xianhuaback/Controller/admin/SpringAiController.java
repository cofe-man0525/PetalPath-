package com.explore.xianhuaback.Controller.admin;

import org.springframework.ai.chat.client.ChatClient;
import com.explore.xianhuaback.Result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import java.time.Duration;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/admin/AI")
public class SpringAiController {

    @Autowired
    private ChatClient chatClient;

    @PostMapping("/chat")
    public Flux<String> chat(@RequestBody Object body) {
        String query = extractQuery(body);
        if (query == null || query.isBlank()) {
            return Flux.just("【提示】query不能为空");
        }

        // 分段缓冲输出：保持“流式逐步返回”，但控制消息粒度，避免前端被超细粒度内容刷爆
        return chatClient.prompt()
                .system("你是一个中文助手，请始终使用中文回答用户的问题。")
                .user(query)
                .stream()
                .content()
                // 每 120ms 或最多 4 段 token 聚合后发给前端（更频繁回显）
                .bufferTimeout(4, Duration.ofMillis(120))
                .map(chunks -> String.join("", chunks))
                .filter(s -> s != null && !s.isEmpty())
                .onErrorResume(e -> Flux.just("【AI请求失败】" + (e.getMessage() == null ? "" : e.getMessage())));
    }

    @PostMapping("/chatOnce")
    public Result<String> chatOnce(@RequestBody Object body) {
        String query = extractQuery(body);
        if (query == null || query.isBlank()) {
            return Result.error("query不能为空");
        }

        try {
            // 复用流式能力，但一次性拼接成完整文本返回，便于前端直接渲染
            String answer = chatClient.prompt()
                    .system("你是一个中文助手，请始终使用中文回答用户的问题。")
                    .user(query)
                    .stream()
                    .content()
                    .reduce(new StringBuilder(), (sb, token) -> sb.append(token))
                    .map(StringBuilder::toString)
                    .block(Duration.ofSeconds(60));

            if (answer == null) {
                return Result.error("AI返回为空");
            }
            return Result.success(answer);
        } catch (Exception e) {
            log.error("AI chatOnce failed", e);
            return Result.error("AI请求失败：" + (e.getMessage() == null ? "" : e.getMessage()));
        }
    }

    private String extractQuery(Object body) {
        if (body == null)
            return null;
        if (body instanceof String) {
            return ((String) body).trim();
        }
        if (body instanceof Map<?, ?> map) {
            Object q = map.get("query");
            if (q instanceof String s && !s.isBlank())
                return s.trim();
            q = map.get("prompt");
            if (q instanceof String s && !s.isBlank())
                return s.trim();
            q = map.get("content");
            if (q instanceof String s && !s.isBlank())
                return s.trim();
        }
        return null;
    }
}