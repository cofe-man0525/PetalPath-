package com.explore.xianhuaback.Controller.user;

import com.explore.xianhuaback.DTO.UserIdDTO;
import com.explore.xianhuaback.Entity.UserShopPoints.ShopUserPointsVO;
import com.explore.xianhuaback.Entity.UserShopPoints.UserPurchasePointsVO;
import com.explore.xianhuaback.Result.Result;
import com.explore.xianhuaback.Service.user.ShopUserPointsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/user")
public class ShopPointsController {

    @Autowired
    private ShopUserPointsService shopUserPointsService;


    @GetMapping("/getShopPoints")
    public Result<List<ShopUserPointsVO>> getShopPoints(){
        log.info("开始进行接收传递数据进行返回");

        List<ShopUserPointsVO> shopUserPointsVO= shopUserPointsService.getShopPoints();
        if(shopUserPointsVO==null){
            log.info("传递过来得数据不存在");
            throw new RuntimeException("传递过来得数据不存在");
        }
        return Result.success(shopUserPointsVO);
    }

    //根据用户的情况来进行返回用户个人的卡卷的情况
    @PostMapping("/getUserPoints")
    public Result<List> getByUserIdShopPoints(@RequestBody UserIdDTO userIdDTO){
        log.info("进行数值的传递的开始");
        if(userIdDTO==null){
            log.info("前端传递过来的数据是不存在的");
            throw new RuntimeException("前端传递过来的数据是不存在的");
        }
        log.info("获取到对应的用户id形式");
        String userId=userIdDTO.getUserId();

        List<UserPurchasePointsVO> list=shopUserPointsService.getByUsersPoints(userId);
        if(list==null){
            log.info("说明数据库中没有这个数据情况");
            return null;
        }
        return Result.success(list);
    }

    //进行兑换积分卷的形式
    @PostMapping("/getRedeemPoints")
    public Result<String> getRedeemPoints(@RequestBody UserIdDTO userIdDTO){

        log.info("已经在接受层中能够查询到对应的数据情况");
        if(userIdDTO==null){
            log.info("前端传递过来的id是不存在的");
            throw new RuntimeException("前端传递过来的id是不存在的");
        }

        String flag= shopUserPointsService.getRedeemPoints(userIdDTO);
        if(flag.equals("兑换成功")){
            log.info("兑换成功");
            return Result.success(flag);
        }else{
            return Result.error(flag);
        }
    }

    @PostMapping("/selectstatus")
    public Result<String> SelectStatusData(@RequestBody UserIdDTO userIdDTO){
        log.info("这里的情况就是将原来的数据进行查询数据更改状态");

        if(userIdDTO==null){
            log.info("这里传递过来的user_id是不存在的");
            throw new RuntimeException("传递过来的用户id不存在的");
        }
        String userId=userIdDTO.getUserId();
        String flag=shopUserPointsService.SelectStatusData(userId);

        if(flag.equals("查询到失效的优惠劵的情况"));
        return Result.success(flag);
    }
}
