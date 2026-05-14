package com.explore.xianhuaback.Controller.user;

import com.explore.xianhuaback.DTO.UserActivityDTO.AddUserActivityDTO;
import com.explore.xianhuaback.DTO.UserLoginDTO.UserGetPointsDTO;
import com.explore.xianhuaback.Entity.ActivityUser.ActivityUserVO;
import com.explore.xianhuaback.Entity.ActivityUser.GetActivityUserVO;
import com.explore.xianhuaback.Result.Result;
import com.explore.xianhuaback.Service.user.ActivityService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserActivityController {

    @Autowired
    private ActivityService activityService;

    @GetMapping("/getActivity")
    public Result<List<ActivityUserVO>> getActivity(){


        List<ActivityUserVO> list=activityService.getActivityList();

        if(list==null||list.size()==0){
            log.info("逻辑层传递过来的数据不存在");
            return null;
        }

        return Result.success(list);
    }

    //根据用户的id来查询用户的数据情况
    @PostMapping("/getUserActivity")
    public Result<List<GetActivityUserVO>> getUserActivity(@RequestBody UserGetPointsDTO userGetPointsDTO){
        log.info("userGetPointsDTO已经进行传递过来了");
        if(userGetPointsDTO==null){
            log.info("判断传递过来的数据不存在");
            throw new RuntimeException("前端传递过来的用户id错误了");
        }

        String userId=userGetPointsDTO.getUserId();

        List<GetActivityUserVO> lists=activityService.getUserActivity(userId);
        if(lists==null||lists.size()==0){
            log.info("传递过来的数据是空的");
            Result.success(lists);
        }else{
            Result.success(lists);
        }
        return Result.success(lists);
    }

    //加入活动的情况
    @PostMapping("/addUserActivity")
    public Result<String> addUserActivity(@RequestBody AddUserActivityDTO addUserActivityDTO){
        log.info("传递过来的id不存在");
        if(addUserActivityDTO.getUserId()==null){
            log.info("传递过啊里的数据不存在");
            throw new RuntimeException("传递过来的数据不存在");
        }

        String userId=addUserActivityDTO.getUserId();
        String activityId=addUserActivityDTO.getActivityId();
        Boolean flag= activityService.addUserActivity(userId,activityId);
        if(flag){
            log.info("加入成功");
            return Result.success("success");
        }else{
            log.info("加入失败");
            Result.error("已经加入了");
        }
        return Result.success("success");

    }


}
