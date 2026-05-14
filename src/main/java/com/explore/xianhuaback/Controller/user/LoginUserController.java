package com.explore.xianhuaback.Controller.user;

import cn.dev33.satoken.stp.StpUtil;
import com.explore.xianhuaback.DTO.UserLoginDTO.UserLoginDTO;
import com.explore.xianhuaback.DTO.UserLoginDTO.UserMessageTotalDTO;
import com.explore.xianhuaback.Entity.user.TokenMessagesVO;
import com.explore.xianhuaback.Entity.user.UserMessageVO;
import com.explore.xianhuaback.Result.Result;
import com.explore.xianhuaback.Service.user.UserLoginService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@Slf4j
public class LoginUserController {


    @Autowired
    private UserLoginService  userLoginService;


    @PostMapping("/login")
    public Result<TokenMessagesVO> LoginGet(@RequestBody UserLoginDTO  userLoginDTO){
        log.info("传递过来的数据");
        if(userLoginDTO.getCode() == null || userLoginDTO.getCode().equals("")){
            log.info("前端传递过来的code为空");
            throw new RuntimeException("前端传递数据出现错误");
        }
        if (userLoginDTO.getAvatarUrl() == null || userLoginDTO.getAvatarUrl().equals("")){
            log.info("传递过来的图片是不能存在的");
            throw new RuntimeException("前端传递过来的图片资源是空");
        }

        if(userLoginDTO.getNickName() == null || userLoginDTO.getNickName().equals("")){
            log.info("前端传递过来的匿名不存在");
            throw new RuntimeException("前端传递过来的匿名是不存在");
        }

        log.info("逻辑层的传递开始");
        Result<TokenMessagesVO> TokenMessage=userLoginService.getLoginMessage(userLoginDTO);

         return TokenMessage;
    }

    //进行个人用户的接口设计
    @PostMapping("/userMessageTotal")
    public Result<UserMessageVO> getUserMessage(@RequestBody UserMessageTotalDTO userMessageTotalDTO){
        log.info("前端传递过来的id数");
        if(userMessageTotalDTO.getId() == null || userMessageTotalDTO.getId().equals("")){
            log.info("前端传递过来的数量为空");
            throw new RuntimeException("前端传递过来的id为空");
        }
        log.info(userMessageTotalDTO.getId());
        String id=userMessageTotalDTO.getId();
        UserMessageVO userMessageVO = userLoginService.getUserMessage(id);
        if(userMessageVO == null){
            log.info("传递过来的数据为空");
            throw new RuntimeException("传递过来的数据为空");
        }
        return Result.success(userMessageVO);
    }





}
