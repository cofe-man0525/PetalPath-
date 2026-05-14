package com.explore.xianhuaback.Service.admin.ServiceImpl;

import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import com.explore.xianhuaback.DTO.AdminLoginDTO;
import com.explore.xianhuaback.DTO.CodeAdminDTO;
import com.explore.xianhuaback.DTO.SubmitAdminDTO;
import com.explore.xianhuaback.DTO.EditPasswordDTO;
import com.explore.xianhuaback.Entity.AdminLogin;
import com.explore.xianhuaback.Entity.CodeAdmin;
import com.explore.xianhuaback.Entity.SubmitAdmin;
import com.explore.xianhuaback.Entity.editPassword;
import com.explore.xianhuaback.Mapper.MapperLoginAdmin;
import com.explore.xianhuaback.Service.admin.ServiceAdminLogin;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class ServiceAdminLoginImpl implements ServiceAdminLogin {

    @Autowired
    private MapperLoginAdmin mapperLoginAdmin;

    // 并入到redis缓存中
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    // 利用sa-token来解决双token问题
    @Override
    public SaTokenInfo LoginBackAdmin(AdminLoginDTO adminLoginDTO) {

        log.info("逻辑的编写过程");
        // 编写实体类进行传递
        AdminLogin adminLogin = new AdminLogin();
        BeanUtils.copyProperties(adminLoginDTO, adminLogin); // 密码和用户名
        adminLogin.setRegisterTime(LocalDateTime.now()); // 注册时间
        AdminLogin adminLoginVO = mapperLoginAdmin.getAdminLogin(adminLogin);

        // 判断用户是否存在
        if (adminLoginVO == null) {
            log.info("账号或密码错误");
            throw new RuntimeException("账号或密码错误");
        }

        // 获得数据登录用户的id情况
        Long userId = mapperLoginAdmin.getUserId(adminLogin.getUserName());

        // 判断用户id是否存在
        if (userId == null) {
            log.info("判断用户是否存在");
            throw new RuntimeException("该用户不存在");
        }

        // 调用登录来进行登录的情况
        StpUtil.login(userId);
        log.info("用户登录成功，ID: {}", userId);
        // 利用框架来生成sa-token
        // 参数是用户ID，Sa-Token会根据这个ID生成一个唯一的token
        SaTokenInfo tokenInfo = StpUtil.getTokenInfo();
        log.info("登陆成功");
        return tokenInfo;
    }

    // 注册方法
    @Override
    public String insertSubmit(SubmitAdminDTO submitAdminDTO) {
        log.info("实现逻辑功能");
        SubmitAdmin submitAdmin = new SubmitAdmin();

        BeanUtils.copyProperties(submitAdminDTO, submitAdmin);
        // 设置对应的赋值属性
        submitAdmin.setRegisterTime(LocalDateTime.now()); // 注册时间
        submitAdmin.getLastLoginTime(); // 最新的登陆时间

        // 接入两层数据表结构
        SubmitAdmin submitAdminVo = mapperLoginAdmin.getSubmitAdmin(submitAdmin.getUserName());
        if (submitAdminVo == null) {
            // 将数据进行插入中
            log.info("如果查到的数据为空则进行将数据插入到数据表中");
            mapperLoginAdmin.insertSubmitAdmin(submitAdmin);
            // 将数据插入到登陆表中
            mapperLoginAdmin.insertLoginAdmin(submitAdmin);
            return "success";
        } else {
            log.info("用户已经注册过");
            // 可以返回给前端提示信
            return "该用户已注册";
        }
    }

    // 获取验证码
    @Override
    public String getAdminCode(CodeAdminDTO codeAdminDTO) {
        CodeAdmin codeAdmin = new CodeAdmin();

        if (codeAdminDTO == null) {
            log.info("逻辑层传递过来的用户为空");
            throw new RuntimeException("参数为空逻辑层");
        } else {
            BeanUtils.copyProperties(codeAdminDTO, codeAdmin);
            // 从数据库登录表查询是否具有此数据
            String name = codeAdmin.getUserName();
            String flag = mapperLoginAdmin.getAdminLoginUser(name);
            if (flag == null) {
                return "查无此人,点击注册吧！";
            } else {
                // 如果有这个人开始发送验证码
                SecureRandom random = new SecureRandom();
                int code = 100000 + random.nextInt(900000); // 生成100000-999999之间的随机数
                // 将验证码存入缓存中进行方便调用
                // 这里分别为key value time 单位
                stringRedisTemplate.opsForValue().set(
                        "userName" + codeAdmin.getUserName(),
                        String.valueOf(code),
                        1,
                        TimeUnit.MINUTES);
                log.info("已存入到redis中");

                return String.valueOf(code); // 转换为字符串
            }
        }

    }

    // 修改密码操作
    @Override
    public String editAdmin(EditPasswordDTO editPasswordDTO) {
        editPassword editPassword = new editPassword();
        // 将前端的数据进行传递过来操作
        BeanUtils.copyProperties(editPasswordDTO, editPassword);
        // 从缓存中取出验证码的操作
        try {
            String key = "userName" + editPassword.getUserName();
            String codeRedis = stringRedisTemplate.opsForValue().get(key); // 一律从缓存中取出来都是字符形式
            if (codeRedis == null) {
                log.info("缓存的验证码失效或者是，不存在");
                throw new RuntimeException("验证码失效重新获取");
            }
            // 将前端的数字转换为字符串的形式
            String inputCodeStr = String.valueOf(editPassword.getCode());
            if (!inputCodeStr.equals(codeRedis)) {
                return "error";
            } else {
                mapperLoginAdmin.PutAdminPassword(editPassword); // 在数据库中进行修改密码
                return "success";
            }

        } catch (Exception e) {
            return "运行异常需要检查";
        }

    }

}
