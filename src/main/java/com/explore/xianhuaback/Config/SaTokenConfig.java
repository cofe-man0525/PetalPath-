package com.explore.xianhuaback.Config;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpUtil;
import com.explore.xianhuaback.Utils.RedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class SaTokenConfig implements WebMvcConfigurer {

    @Autowired
    private RedisUtils redisUtils;

    //固定的对应的情况进行解决
    private static final String TOKEN_KEY_PREFIX = "authorization:";

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new SaInterceptor(handle -> {
                    // 1. 先检查 Sa-Token 登录状态
                    try {
                        StpUtil.checkLogin();
                    } catch (NotLoginException e) {
                        System.out.println("未登录: " + e.getMessage());
                        SaRouter.back(401);
                        return;
                    }

                    // 2. 获取当前用户ID和token
                    Long userId = StpUtil.getLoginIdAsLong();
                    String currentToken = StpUtil.getTokenValue();


                    // 3. 检查Redis中是否有这个用户的token
                    String cacheKey = TOKEN_KEY_PREFIX + userId;
                    Object cachedToken = redisUtils.getCache(cacheKey);

                    if (cachedToken == null) {
                        System.out.println("Redis中不存在token，需要重新登录");
                        SaRouter.back(401);
                        return;
                    }

                    if (!cachedToken.toString().equals(currentToken)) {
                        System.out.println("token不匹配，需要重新登录");
                        SaRouter.back(401);
                        return;
                    }

                    System.out.println("token验证通过");
                }))
                .addPathPatterns("/**")
                .excludePathPatterns(

                        "/ws",           // WebSocket 连接端点
                        "/ws/**",        // WebSocket 所有子路径
                        "/",             // 根路径（可选）
                        "/admin/login",
                        "/admin/submit",
                        "/user/login",
                        "/user/register",
                        "/user/sendCode",
                        "/user/getFiveImages",
                        "/user/goodsFlowers",
                        "/admin/Points/**",
                        "/public/**",
                        "/swagger/**",
                        "/doc.html"
                );
    }
}