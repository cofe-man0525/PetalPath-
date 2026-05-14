package com.explore.xianhuaback.Controller.user;

import com.explore.xianhuaback.Result.Result;
import com.explore.xianhuaback.Service.user.SnowFlakeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/user")
public class SnowFlakeController {



    @Autowired
    private SnowFlakeService snowFlakeService;

    //生成对应的雪花算法id进行传递
    public Result<Map<String, Long>> snowFlake(@RequestParam Integer UserId) {
        log.info("进行查找对应的订单编号生成");

        if(UserId==null){
            log.info("进行传递的过程中用户id不存在的");
            throw new RuntimeException("前端传递过来的id不存在的");
        }

        Long snowFlakeId=snowFlakeService.createSnowFlake(UserId);

        if(snowFlakeId==null){
            log.info("生成失败");
            throw new RuntimeException("生成失败");
        }
        Map<String,Long> map=new HashMap<>();
        map.put("用户id", Long.valueOf(UserId));
        map.put("雪花算法的id为:",snowFlakeId);

        return  Result.success(map);
    }
}
