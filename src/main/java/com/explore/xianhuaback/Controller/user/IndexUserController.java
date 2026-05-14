package com.explore.xianhuaback.Controller.user;

import com.explore.xianhuaback.DTO.UserLoginDTO.UserAddressEditDTO;
import com.explore.xianhuaback.DTO.UserLoginDTO.UserGetPointsDTO;
import com.explore.xianhuaback.DTO.UserLoginDTO.UserMessageTotalDTO;
import com.explore.xianhuaback.Entity.user.UserGoodsVO;
import com.explore.xianhuaback.Entity.user.UserMessageVO;
import com.explore.xianhuaback.Entity.user.banner;
import com.explore.xianhuaback.Entity.user.getUserAddressVO;
import com.explore.xianhuaback.Entity.userIndex.UserIndexSignVO;
import com.explore.xianhuaback.Result.Result;
import com.explore.xianhuaback.Service.user.IndexUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/user")
public class IndexUserController {

    @Autowired
    private IndexUserService indexUserService;


    //接收微信小程序的5张图片
    @GetMapping("/getFiveImages")
    public  Result< List<banner>> getFiveImages(){
        log.info("getFiveImages");
        List<banner> list=indexUserService.getFiveImages();

        if(!list.isEmpty()){
            log.info("传递过来的数据是对的");
            return Result.success(list);
        }else{
            log.info("传递过来的数据是错误的");
            throw new RuntimeException("返回来的数据是错误的");
        }
    }

    @GetMapping("/goodsFlowers")
    public Result<List<UserGoodsVO>> getGoodsFlowers(){
        log.info("getGoodsFlowers");
        List<UserGoodsVO> list=indexUserService.getGoodsList();
        if(list.isEmpty()){
            log.info("传递过来的数据是空的");
            throw new RuntimeException("数据库数据为空");
        }

        return Result.success(list);
    }

    //根据号对应的api接口来设计出对应的接口设计问题
    @PostMapping("/useraddress")
    public Result<List<getUserAddressVO>> getUserAddress(@RequestBody UserMessageTotalDTO userMessageTotalDTO){
        log.info("进行用户id的传递形式进行改变用户的地址的情况");
        if(userMessageTotalDTO.getId()==null){
            log.info("前端传递过来的id是空的");
            throw new RuntimeException("检查前端传递过来的id");
        }
        List<getUserAddressVO> list=indexUserService.getUserAddress(userMessageTotalDTO.getId());
        if(list.isEmpty()){
            log.info("可能没有新建的新地址情况");
            return  Result.success(list);
        }

        return Result.success(list);
    }

    //根据id进行回显地址
    @PostMapping("/getByIdAddress")
    public Result<getUserAddressVO> getByIdAddress(@RequestBody  UserMessageTotalDTO userMessageTotalDTO ){
        log.info("进行数值的传递操作");
        if (userMessageTotalDTO.getId()==null){
            log.info("传递过来的id不存在的");
            throw new RuntimeException("数值传递过来的不存在");
        }
        log.info("进行数值的传递");
        String id=userMessageTotalDTO.getId();
        getUserAddressVO object=indexUserService.getByIdAddress(id);
        if(object==null){
            log.info("数据库返回来的数据是不存在的");
            throw new RuntimeException("数据库返回来的数据是不存在的");
        }
        return Result.success(object);

    }

    //根据id进行删除操作
    @PostMapping("/deteleAddress")
    public Result<String> deleteAddress(@RequestBody UserMessageTotalDTO userMessageTotalDTO){
        if(userMessageTotalDTO.getId()==null){
            log.info("前端传递过来的数据的是空的");
            throw new RuntimeException("前端传递过来的数据为空");
        }
        String id=userMessageTotalDTO.getId();
        indexUserService.deleteById(id);
        return  Result.success();
    }

    //根据id来进行编辑保存内容
    @PostMapping("/saveAddress")
    public Result<String> saveAddress(@RequestBody UserAddressEditDTO userAddressEditDTO){
        log.info(userAddressEditDTO.getUserId());
            if(userAddressEditDTO.getCity()==null
            || userAddressEditDTO.getName()==null || userAddressEditDTO.getPhone()==null){
                log.info("前端传递过来的数据也可能是空的");
                //抽检实验结果
                throw new RuntimeException("前端返回来的数据是空的");
            }
            //进行编辑的操作进行完成
            Boolean flag=indexUserService.editSaveAddress(userAddressEditDTO);
            if(flag){
                log.info("传递过来的数据是正确的");
                return Result.success();
            }
            return Result.error("error");
    }

    //进行添加地址的情况
    @PostMapping("/addAddress")
    public Result<String> addAddress(@RequestBody UserAddressEditDTO userAddressEditDTO){
        log.info("添加地址的情况");
        try{
            log.info("进行逻辑传递的情况");
            if(userAddressEditDTO.getCity()==null
                    ||userAddressEditDTO.getName()==null){
                log.info("前端传递过来的数据是空的");
                throw new RuntimeException("请查看前端的传递");
            }
            //进行逻辑判断的情况
            Boolean flag=indexUserService.addAddress(userAddressEditDTO);
            if(flag){
                log.info("逻辑层传递过来的判断的情况是正确的");
                return Result.success();
            }else{
                return Result.error("传递的数据有问题");
            }
        }catch (Exception e){
            log.info("进行传递的情况");
            throw new RuntimeException(e);
        }
    }

    //查询积分的情况
    //要将积分进行签到的数据进行返回的操作
    @PostMapping("/SignPoints")
    public Result<String> getPoints(@RequestBody UserGetPointsDTO userGetPointsDTO){
        log.info("进行添加积分的情况");
        if(userGetPointsDTO.getUserId()==null){
            log.info("进行传递的id是空的");
            throw new RuntimeException("前端传递过来的id是空的");
        }

        Boolean flag= indexUserService.getCheckPoints(userGetPointsDTO);
        if(flag){

            return Result.success("已经签到");
        }else{
            return Result.error("还未签到签到");
        }
    }

    @PostMapping("/getPoints")
    public Result<List<UserIndexSignVO>> getHistoryPoints(@RequestBody UserGetPointsDTO userGetPointsDTO){
        //进行展示的情况进行查询历史积分情况
        log.info("进行查询历史积分的情况");
        if(userGetPointsDTO.getUserId()==null){
            log.info("前端传递过来的user_id不存在");
            throw new RuntimeException("前端传递过来的数据不存在");
        }

        String userId=userGetPointsDTO.getUserId();
        List<UserIndexSignVO> list=indexUserService.getHistoryPoints(userId);
        if(list.isEmpty()){
            log.info("数据逻辑层传递过来的数据为空");
            throw new RuntimeException("查看逻辑层");
        }
        return  Result.success(list);
    }

    //插入数据库的积分状态
    @PostMapping("/insertPoints")
    //指定的都是用户id情况
    public Result<UserMessageVO> insertPoints(@RequestBody UserGetPointsDTO userGetPointsDTO){
        if(userGetPointsDTO.getUserId()==null){
            log.info("前端返回来的数据id不存在");
            throw new RuntimeException("查看前端传递过来的数据情况");
        }
        String userId=userGetPointsDTO.getUserId();
        UserMessageVO userMessageVO=indexUserService.insertPoints(userId);
        if(userMessageVO.getUserId()==null){
            log.info("逻辑层传递对的是错误的");
            throw new RuntimeException("传递的是错误的");
        }
        return Result.success(userMessageVO);
    }


    //已经连续签到一周了
    @PostMapping("/insertPointsWeek")
    public Result<String>insertPointsWeek(@RequestBody UserGetPointsDTO userGetPointsDTO){
        log.info("连续签到一周");
        if(userGetPointsDTO.getUserId()==null){
            log.info("进行连续签到一周");
            throw new RuntimeException("前端传递过来的user_id不存在");
        }

        String userId=userGetPointsDTO.getUserId();
        indexUserService.insertPointsWeek(userId);
        return Result.success();
    }

    //已经连续签到一个月
    @PostMapping("/insertPointsMonth")
    public Result<String>insertPointsMonth(@RequestBody UserGetPointsDTO userGetPointsDTO){
        log.info("连续签到一个月");
        if(userGetPointsDTO.getUserId()==null){
            log.info("连续签到一个月");
            throw new RuntimeException("前端传递过来的user_id不存在");
        }

        String userId=userGetPointsDTO.getUserId();
        indexUserService.insertPointsMonth(userId);
        return Result.success();
    }

}
