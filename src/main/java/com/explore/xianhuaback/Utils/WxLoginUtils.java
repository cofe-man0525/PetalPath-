package com.explore.xianhuaback.Utils;

import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component  // ← 加上这个注解
@Slf4j
public class WxLoginUtils {

    private static final String WX_BASE_URL = "https://api.weixin.qq.com/sns/jscode2session?appid=%s&secret=%s&js_code=%s&grant_type=authorization_code";

    // 从配置文件读取，不是从对象里拿
    @Value("${wechat.appid}")
    private String appId;

    @Value("${wechat.secret}")
    private String appSecret;

    @Autowired
    private RestTemplate restTemplate;

    // 使用code来置换openId
    public String getByOpenId(String code) {
        log.info("开始获取openId, code: {}", code);

        // 1. 校验参数
        if (code == null || code.isEmpty()) {
            log.error("传递过来的code为空");
            throw new RuntimeException("code不能为空");
        }

        try {
            // 2. 构建请求地址（使用配置的appId和appSecret）
            String url = String.format(WX_BASE_URL, appId, appSecret, code);
            log.info("请求微信接口: {}", url);

            // 3. 调用微信接口
            String result = restTemplate.getForObject(url, String.class);
            log.info("微信返回结果: {}", result);

            // 4. 解析结果
            JSONObject resultJson = JSONObject.parseObject(result);

            // 5. 检查是否有错误（注意：微信返回的是 errcode，不是 errCode）
            if (resultJson.containsKey("errcode")) {
                Integer errcode = resultJson.getInteger("errcode");
                String errmsg = resultJson.getString("errmsg");
                log.error("微信接口返回错误: errcode={}, errmsg={}", errcode, errmsg);
                throw new RuntimeException("微信登录失败: " + errmsg);
            }

            // 6. 获取openId
            String openId = resultJson.getString("openid");
            if (openId == null || openId.isEmpty()) {
                log.error("获取openId失败");
                throw new RuntimeException("获取openId失败");
            }

            log.info("获取openId成功: {}", openId);
            return openId;

        } catch (Exception e) {
            log.error("调用微信接口异常", e);
            throw new RuntimeException("调用微信接口失败: " + e.getMessage());
        }
    }
}