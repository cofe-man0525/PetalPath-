package com.explore.xianhuaback.Service.user.Impl;


import com.explore.xianhuaback.Service.user.SnowFlakeService;
import com.explore.xianhuaback.Utils.SnowflakeIdWorker;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Select;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SnowFlakesServiceImpl implements SnowFlakeService {

    @Autowired
    private SnowflakeIdWorker snowflakeIdWorker;
    @Override
    public Long createSnowFlake(Integer userId) {
        if(userId==null){
            log.info("传递过来的鲜花id为空的");
        }

        log.info("传递给前端的雪花算法id");
        Long snowId=snowflakeIdWorker.nextId();

        return snowId;

    }
}
