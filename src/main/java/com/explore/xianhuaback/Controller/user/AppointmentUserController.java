package com.explore.xianhuaback.Controller.user;


import com.explore.xianhuaback.DTO.AppointmentUserDTO.AppointmentUserDTO;
import com.explore.xianhuaback.Result.Result;
import com.explore.xianhuaback.Service.user.AppointmentUserService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/user")
public class AppointmentUserController {

    //websocket通知前端消息给对应的回复功能
    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private AppointmentUserService appointmentUserService;

    //添加上了对应的接口限流和熔断器
    @PostMapping("/addAppointment")
    @RateLimiter(name = "appointment-limiter")
    @CircuitBreaker(name = "appointment-breaker", fallbackMethod = "fallback")
    public Result<String> addAppointment(@RequestBody AppointmentUserDTO appointmentUserDTO){
            if(appointmentUserDTO.getContent() == null){
                log.info("对应传递过来的备注没有存在的");
                throw new RuntimeException("前端传递过来的数据出现异常");
            }

            if (appointmentUserDTO.getName() == null){

                log.info("传递过来的数据是不存在的");
                throw new RuntimeException("传递过来的姓名出现了错误");
            }

            if (appointmentUserDTO.getType() == null){
                log.info("传递过来的数据类型不存在的");
                throw new RuntimeException("传递过来的类型不存在的");
            }
            log.info("写入对应的排队系统中");
            try{
                log.info("进入排队行列");
                String flag =appointmentUserService.joinWaitingQueue(appointmentUserDTO);

                if(flag.equals("已经加入排队中，请等待通知")){

                    return Result.success("已经加入排队中，请等待通知");

                }else if(flag.equals("队伍已经满了，请稍后重试")){

                    return Result.error("队伍已经满了，请稍后重试");
                }else{
                    return Result.error("你已经排队了");
                }
            }catch (Exception e){

                log.error("系统繁忙");
                return  Result.error("系统繁忙");
            }

    }
}
