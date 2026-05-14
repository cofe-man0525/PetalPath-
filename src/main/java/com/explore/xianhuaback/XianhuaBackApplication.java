package com.explore.xianhuaback;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties  // 启用配置属性
public class XianhuaBackApplication {

    public static void main(String[] args) {
        SpringApplication.run(XianhuaBackApplication.class, args);
    }

}
