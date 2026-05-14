package com.explore.xianhuaback.Controller.admin;

import cn.dev33.satoken.stp.SaTokenInfo;
import com.explore.xianhuaback.DTO.AdminLoginDTO;
import com.explore.xianhuaback.DTO.CodeAdminDTO;
import com.explore.xianhuaback.DTO.SubmitAdminDTO;
import com.explore.xianhuaback.DTO.EditPasswordDTO;
import com.explore.xianhuaback.Result.Result;
import com.explore.xianhuaback.Service.admin.ServiceAdminLogin;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/admin")
public class LoginAdminController {

    @Autowired
    private ServiceAdminLogin serviceAdminLogin;

    @PostMapping("/login")
    public Result<SaTokenInfo> AdminLogin(@RequestBody AdminLoginDTO adminLoginDTO) {
        log.info("传递参数开始");

        try{
            log.info("尝试解决问题内容部分");
            SaTokenInfo tokenInfo=serviceAdminLogin.LoginBackAdmin(adminLoginDTO);
            return Result.success(tokenInfo);
        }catch (Exception e) {
            e.printStackTrace();
            return Result.error(e.getMessage() == null ? "登录失败" : e.getMessage());
        }
    }

    @PostMapping("/submit")
    public Result<String> AdminSubmit(@RequestBody SubmitAdminDTO submitAdminDTO) {
        if (submitAdminDTO == null) {
            return Result.error("提交数据不能为空");
        }
        if (submitAdminDTO.getPassWord() == null || submitAdminDTO.getPassWord().trim().isEmpty()) {
            return Result.error("密码不能为空");
        }

        try{
            log.info("尝试解决相关内容");
            String msg=serviceAdminLogin.insertSubmit(submitAdminDTO);
            if(msg.equals("success")){
                return Result.success(msg);
            }else{
                return Result.error(msg);
            }
        }catch(Exception e){
            e.printStackTrace();
            return Result.error("用户注册失败");
        }

    }

    //获取验证码
    @PostMapping("/code")
    public Result<String> getAdminCode(@RequestBody CodeAdminDTO codeAdminDTO) {
        if (codeAdminDTO == null) {
            throw new RuntimeException("传递过来的用户不存在");
        }
        try{
            String code=serviceAdminLogin.getAdminCode(codeAdminDTO);
            if(code==null){

                throw new RuntimeException("验证码不存在");
            }else if(code.equals("查无此人,点击注册吧")){
                return Result.error(code);
            }else{
                return Result.success(code);
            }

        }catch(Exception e){
            e.printStackTrace();
            throw new RuntimeException("传递失败");
        }
    }

    //修改密码的操作
    @PutMapping("/editPassword")
    public Result<String> EditPutAdmin(@RequestBody EditPasswordDTO editPasswordDTO){

        if(editPasswordDTO == null){
            throw  new RuntimeException("传递的参数为空请查看前端");
        }
        log.info("传递形式开始");
        try{
            String flag=serviceAdminLogin.editAdmin(editPasswordDTO);
            if(flag.equals("error")){
                return Result.error("验证码不一致");
            }else{

                return Result.success(flag);
            }
        }catch(Exception e){
            e.printStackTrace();
            throw new RuntimeException("运行错误失效");
        }

    }
}
