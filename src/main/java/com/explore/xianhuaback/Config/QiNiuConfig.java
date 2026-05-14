package com.explore.xianhuaback.Config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "qiniu")  // 这个很重要！
public class QiNiuConfig {

    private String accessKey;
    private String secretKey;
    private String bucket;
    private String domain;
}
