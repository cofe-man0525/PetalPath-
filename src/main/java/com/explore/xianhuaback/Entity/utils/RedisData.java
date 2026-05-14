package com.explore.xianhuaback.Entity.utils;

import lombok.Data;

import java.util.concurrent.TimeUnit;

@Data
public class RedisData {

    private Object data; //设计的逻辑数据

    private Long expireTime;  //逻辑时间

    public void setTimeToLive(Long time, TimeUnit timeUnit) {
        this.expireTime = System.currentTimeMillis() + timeUnit.toMillis(time);
    }

    public boolean isExpired() {
        return expireTime != null && System.currentTimeMillis() > expireTime;
    }

}
