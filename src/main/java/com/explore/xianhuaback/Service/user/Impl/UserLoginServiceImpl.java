package com.explore.xianhuaback.Service.user.Impl;

import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.json.JSONUtil;
import com.explore.xianhuaback.DTO.UserLoginDTO.UserLoginDTO;
import com.explore.xianhuaback.Entity.user.LoginUserVO;
import com.explore.xianhuaback.Entity.user.TokenMessagesVO;
import com.explore.xianhuaback.Entity.user.UserMessageVO;
import com.explore.xianhuaback.Mapper.user.LoginUserMapper;
import com.explore.xianhuaback.Result.Result;
import com.explore.xianhuaback.Service.user.UserLoginService;
import com.explore.xianhuaback.Utils.RedisUtils;
import com.explore.xianhuaback.Utils.WxLoginUtils;
import io.micrometer.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class UserLoginServiceImpl implements UserLoginService {

    private static final String PHONE_NUMBER_DEFAULT = "15837000263";
    private static final String TOKEN_NUMBER_AUTH = "authorization:";  // 修改：统一格式
    private static final String USER_ID = "user_id:";

    @Autowired
    private LoginUserMapper loginUserMapper;

    @Autowired
    private WxLoginUtils wxLoginUtils;

    @Autowired
    private RedisUtils redisUtils;


    //进行登录的情况
    @Override
    public Result<TokenMessagesVO> getLoginMessage(UserLoginDTO userLoginDTO) {
        // 1. 参数校验
        if (userLoginDTO.getCode() == null || userLoginDTO.getCode().isEmpty()) {
            throw new RuntimeException("code不能为空");
        }
        String code = userLoginDTO.getCode();

        // 2. 微信登录获取openId
        String openId = wxLoginUtils.getByOpenId(code);
        if (openId == null) {
            throw new RuntimeException("获取openId失败");
        }

        // 3. 查询用户是否存在
        LoginUserVO loginUserVO = loginUserMapper.getByOpenId(openId);

        if (loginUserVO == null) {
            // ========== 新用户：创建 ==========
            LoginUserVO loginUser = new LoginUserVO();
            loginUser.setOpenId(openId);
            loginUser.setAvatarUrl(userLoginDTO.getAvatarUrl());
            loginUser.setPhone(PHONE_NUMBER_DEFAULT);
            loginUser.setNickName(userLoginDTO.getNickName());
            loginUser.setIsDeleted(0);
            loginUser.setStatus(1);
            loginUser.setCreateTime(LocalDateTime.now());
            loginUser.setUpdateTime(LocalDateTime.now());
            loginUser.setTotalPoints(0);
            loginUser.setTotalOrderCount(0);

            Boolean flag = loginUserMapper.insertLoginUser(loginUser);
            if (!flag) {
                throw new RuntimeException("插入用户失败");
            }

            // 重新查询获取自增id
            loginUserVO = loginUserMapper.getByOpenId(openId);

            // ✅ 登录
            StpUtil.login(loginUserVO.getId());
            SaTokenInfo tokenInfo = StpUtil.getTokenInfo();
            String tokenValue = tokenInfo.tokenValue;

            // 存入Redis
            String cacheKey = TOKEN_NUMBER_AUTH + loginUserVO.getId();
            redisUtils.setCache(cacheKey, tokenValue, 1, TimeUnit.DAYS);

            // 返回数据
            TokenMessagesVO tokenMessagesVo = new TokenMessagesVO();
            tokenMessagesVo.setToken(tokenValue);
            tokenMessagesVo.setAvatarUrl(loginUser.getAvatarUrl());
            tokenMessagesVo.setNickname(loginUser.getNickName());
            tokenMessagesVo.setOpenid(openId);

            return Result.success(tokenMessagesVo);

        } else {
            // ========== 老用户：每次都重新登录

            // ✅ 关键：必须调用登录
            StpUtil.login(loginUserVO.getId());
            SaTokenInfo tokenInfo = StpUtil.getTokenInfo();
            String tokenValue = tokenInfo.tokenValue;


            // 存入Redis（覆盖旧的）
            String cacheKey = TOKEN_NUMBER_AUTH + loginUserVO.getId();
            redisUtils.setCache(cacheKey, tokenValue, 1, TimeUnit.DAYS);

            // 验证是否存储成功
            Object verifyCache = redisUtils.getCache(cacheKey);
            log.info("Redis存储验证: key={}, value={}", cacheKey, verifyCache);

            // 验证Sa-Token是否生效
            try {
                StpUtil.checkLogin();
                log.info("Sa-Token验证通过 ✅");
            } catch (Exception e) {
                log.error("Sa-Token验证失败 ❌: {}", e.getMessage());
            }

            // 返回数据
            //针对与对应的反应数据总结情况
            TokenMessagesVO tokenMessagesVo = new TokenMessagesVO();
            tokenMessagesVo.setToken(tokenValue);
            tokenMessagesVo.setAvatarUrl(loginUserVO.getAvatarUrl());
            tokenMessagesVo.setNickname(loginUserVO.getNickName());
            tokenMessagesVo.setOpenid(openId);

            return Result.success(tokenMessagesVo);
        }
    }

    //进行接收的情况进行数据的转换情况
    @Override
    public UserMessageVO getUserMessage(String id) {
        log.info("前端传递过来的Id: {}", id);

        if (id == null || id.isEmpty()) {
            log.error("接收层出现了问题，id为空");
            throw new RuntimeException("接收层出现了问题");
        }

        //查询用户的数据情况
        log.info("根据openId查询用户主键ID: {}", id);
        Long userID = loginUserMapper.getUserMessage(id);

        if (userID == null) {
            log.error("用户不存在，openId: {}", id);
            throw new RuntimeException("用户不存在");
        }

        //用户的数量的统计
        log.info("查询用户统计数据，userID: {}", userID);
        //首先统计的是缓存中
        Object json = redisUtils.queryWithSetCache(userID, USER_ID,
                UserMessageVO.class,
                ids -> loginUserMapper.getByTotalNumberUser(ids));

        if (json == null) {
            log.info("用户统计数据不存在，初始化默认数据，userID: {}", userID);

            UserMessageVO userMessageVO = new UserMessageVO();
            userMessageVO.setUserId(userID);
            userMessageVO.setTotalMessages(0);
            userMessageVO.setTotalPoints(0);
            userMessageVO.setTotalActivities(0);
            userMessageVO.setTotalCoupons(0);
            userMessageVO.setTotalOrders(0);
            userMessageVO.setTotalAppointments(0);
            userMessageVO.setTotalVisitsActivities(0);
            userMessageVO.setCartItemsCount(0);

            //设计的对应数据库外键设置
            Boolean flag = loginUserMapper.insertUserMessage(userID);
            if (!flag) {
                log.error("插入用户统计数据失败，userID: {}", userID);
                throw new RuntimeException("插入失败请查看数据库");
            }

            String keyPrefix = USER_ID + userID;
            redisUtils.setCache(keyPrefix, userMessageVO, 1, TimeUnit.DAYS);
            return userMessageVO;
        }
        // 解析缓存数据进行返回的情况
        UserMessageVO result = null;
        if (json instanceof String) {
            String jsonStr = (String) json;
            if (StringUtils.isNotBlank(jsonStr)) {
                result = JSONUtil.toBean(jsonStr, UserMessageVO.class);
            }
        } else if (json instanceof UserMessageVO) {
            result = (UserMessageVO) json;
        } else {
            log.warn("未知的缓存数据类型: {}", json.getClass());
        }
        return result;
    }
}