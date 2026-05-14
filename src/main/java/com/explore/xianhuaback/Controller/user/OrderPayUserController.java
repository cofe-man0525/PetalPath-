package com.explore.xianhuaback.Controller.user;

import com.explore.xianhuaback.DTO.UserOrderDTO.OrderPayUserDTO;
import com.explore.xianhuaback.Result.Result;
import com.explore.xianhuaback.Service.user.OrderPayUserService;
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
public class OrderPayUserController {

    //websocket通知给前端的请求给前端
    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private OrderPayUserService orderPayUserService;

    //将订单进行加入到对应的消息队列中，让后端的逻辑为异步操作
    @PostMapping("/payOrder")
    @RateLimiter(name = "appointment-limiter")
    @CircuitBreaker(name = "appointment-breaker", fallbackMethod = "fallback")
    public Result<String> orderCreate(@RequestBody OrderPayUserDTO orderPayUserDTO){
            log.info("订单的传递过来到接收层未到消息队列中");

            if(orderPayUserDTO.getUserId()==null||
                    orderPayUserDTO.getAddressId()==null ||
                    orderPayUserDTO.getCouponId()==null||
                    orderPayUserDTO.getDiscountAmount()==null||
                    orderPayUserDTO.getPayAmount()==null||
                    orderPayUserDTO.getGoodsNumber()==null
            ){
                log.info("前端传递过来的数据中id或者数量不对或不存在");
                throw new RuntimeException("接收层的数据传递不对");
            }

            try{
                log.info("开始进行队列传递");

                String flag=orderPayUserService.jointOrderCreate(orderPayUserDTO);
                if(flag.equals("success")){

                    log.info("加入成功");
                    return Result.success("进行确定支付的界面");
                }else{
                    return Result.error("网络异常请重试");
                }
            }catch (Exception e){
                log.info("产生对应的异常情况"+e.getMessage());
                throw new RuntimeException("逻辑出现错误");

            }
    }
}
