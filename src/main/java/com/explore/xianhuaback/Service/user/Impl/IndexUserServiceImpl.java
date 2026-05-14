package com.explore.xianhuaback.Service.user.Impl;

import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.explore.xianhuaback.Config.RedisConfig;
import com.explore.xianhuaback.DTO.UserLoginDTO.UserAddressEditDTO;
import com.explore.xianhuaback.DTO.UserLoginDTO.UserGetPointsDTO;
import com.explore.xianhuaback.Entity.user.*;
import com.explore.xianhuaback.Entity.userIndex.UserIndexSignVO;
import com.explore.xianhuaback.Mapper.user.IndexUserMapper;
import com.explore.xianhuaback.Mapper.user.LoginUserMapper;
import com.explore.xianhuaback.Result.Result;
import com.explore.xianhuaback.Service.user.IndexUserService;
import com.explore.xianhuaback.Utils.RedisUtils;
import io.micrometer.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class IndexUserServiceImpl implements IndexUserService {

    // 要缓存的key和value(五张图片)
    private static final String BANNER_CACHE_KEY = "INDEX_banner_list_key";

    private static final String GOODS_CACHE_KEY = "INDEX_goods_list_key";

    // 用户的id进行回改
    private static final String USER_INDEX_POINTS = "INDEX_USER_POINTS";

    private static final String ONE_USER_ADDRESS = "one_user_address";
    // 保存缓存的过期时间
    private static final long BANNER_TIME = 12;

    private static final String USER_ID = "user_id";

    // 签到的过期时间的检索(指定用户的签到的判断)
    private static final String USER_INDEX_SIGN_TIME = "user_index_sign_time";

    // 针对个人用户的总积分的情况
    private static final String GET_INDEX_HISTORY_POINTS = "get_index_history_points";

    @Autowired
    private IndexUserMapper indexUserMapper;

    @Autowired
    private LoginUserMapper loginUserMapper;

    @Autowired
    private RedisConfig redisConfig;

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public List<banner> getFiveImages() {
        List<banner> lists = (List<banner>) redisConfig.redisTemplate().opsForValue().get(BANNER_CACHE_KEY);
        if (lists == null || lists.isEmpty()) {
            List<banner> listMysql = indexUserMapper.getFiveImages();
            if (listMysql == null || listMysql.isEmpty()) {
                // 数据库无数据时的处理
            } else {
                redisConfig.redisTemplate().opsForValue().set(BANNER_CACHE_KEY, listMysql, BANNER_TIME, TimeUnit.HOURS);
                return listMysql;
            }
        } else {
            return lists;
        }
        return null;
    }

    // 获取数据库的数据
    @Override
    public List<UserGoodsVO> getGoodsList() {
        Object cachedValue = redisConfig.redisTemplate().opsForValue().get(GOODS_CACHE_KEY);
        if (cachedValue != null) {
            List<UserGoodsVO> list = null;
            if (cachedValue instanceof String) {
                String json = (String) cachedValue;
                if (StringUtils.isNotBlank(json)) {
                    list = JSONUtil.toList(json, UserGoodsVO.class);
                    return list;
                }
            } else if (cachedValue instanceof List) {
                list = (List<UserGoodsVO>) cachedValue;
                return list;
            }
            if (list == null || list.isEmpty()) {
                log.info("缓存中也没有存在");
                throw new RuntimeException("进行缓存的形式也是不存在的");
            }
        }
        List<UserGoodsVO> userLists = redisUtils.queryListWithSetCache(GOODS_CACHE_KEY,
                UserGoodsVO.class,
                () -> indexUserMapper.getGoodsList());

        if (userLists == null || userLists.isEmpty()) {
            // 数据库无数据时的处理
        } else {
            log.info("出现了错误");
            return userLists;
        }
        return null;
    }

    @Override
    public List<getUserAddressVO> getUserAddress(String id) {
        if (id == null) {
            // id为空的处理
        }
        List<getUserAddressVO> lists = indexUserMapper.getUserAddress(id);
        if (lists == null || lists.isEmpty()) {
            throw new RuntimeException("数据库数据不存在");
        }
        return lists;
    }

    // 根据id进行回显操作
    @Override
    public getUserAddressVO getByIdAddress(String id) {
        if (id == null) {
            // id为空的处理
        }
        getUserAddressVO object = indexUserMapper.getByIdAddress(id);
        if (object == null) {
            throw new RuntimeException("数据库数据不存在");
        }
        return object;
    }

    // 根据id删除目标数据
    @Override
    public void deleteById(String id) {
        int result = indexUserMapper.deleteById(id);
        if (result <= 0) {
            log.info("删除对应的地址代码形式");
            throw new RuntimeException("删除对应的代码形式");
        }
        log.info("已经删除了");
    }

    // 进行编辑的操作
    @Override
    @Async
    public Boolean editSaveAddress(UserAddressEditDTO userAddressEditDTO) {
        if (userAddressEditDTO.getId() == null) {
            // id为空的处理
        }
        userAddressEditDTO.setCreateTime(String.valueOf(LocalDateTime.now()));
        userAddressEditDTO.setUpdateTime(String.valueOf(LocalDateTime.now()));

        if (userAddressEditDTO.getIsDefault() == 1) {
            log.info("前端传递过来的属性数值为");
            Boolean flag = indexUserMapper.editAddressIsDefault(userAddressEditDTO);
            if (!flag) {
                log.info("插入的数据库问题进行处理解决");
                throw new RuntimeException("数据库的问题没有进行解决");
            }
        }

        Boolean flag = indexUserMapper.editSaveAddress(userAddressEditDTO);
        if (flag) {
            // 编辑成功
        } else {
            log.info("数据库的操作失败请查看数据层");
            throw new RuntimeException("查看控制台的操作");
        }
        return true;
    }

    @Override
    @Async
    public Boolean addAddress(UserAddressEditDTO userAddressEditDTO) {
        try {
            log.info("逻辑判断开始");
            userAddressEditDTO.setCreateTime(String.valueOf(LocalDateTime.now()));
            userAddressEditDTO.setUpdateTime(String.valueOf(LocalDateTime.now()));

            if (userAddressEditDTO.getIsDefault() != null && userAddressEditDTO.getIsDefault() == 1) {
                log.info("设置为默认地址，需要将其他的地址修改为非默认");
                Boolean flag = indexUserMapper.editAddressIsDefault(userAddressEditDTO);
                if (flag) {
                    log.info("取消其他默认地址成功");
                }
            }

            Thread.sleep(500);
            Boolean flag = indexUserMapper.addAddress(userAddressEditDTO);
            if (flag) {
                log.info("地址添加成功");
                return true;
            } else {
                log.error("数据库增加失败");
                throw new RuntimeException("数据库增加失败");
            }
        } catch (Exception e) {
            log.error("添加地址异常: {}", e.getMessage(), e);
            throw new RuntimeException("接收层的数据情况是错误的: " + e.getMessage());
        }
    }

    // 进行签到的情况
    @Override
    public Boolean getCheckPoints(UserGetPointsDTO userGetPointsDTO) {
        log.info("进行数值的传递的情况");
        if (userGetPointsDTO.getUserId() == null) {
            log.info("这里进行传递的前端出现了问题");
            throw new RuntimeException("接收层出现了问题");
        }
        log.info("查询数据库的情况，来进行查询缓存的情况");
        String userId = userGetPointsDTO.getUserId();
        String key = USER_INDEX_SIGN_TIME + userId;
        log.info("进行拼接用户的数据的情况");
        Object jsonObject = redisUtils.getCache(key);

        if (jsonObject == null) {
            log.info("缓存传递过来的数据是空的");
            Boolean flag = indexUserMapper.insertPointsSign(userId);
            if (flag) {
                log.info("已经插入数据中了");
                UserIndexSignVO userIndexSignVO = indexUserMapper.getSignPoints(userId);
                LocalDateTime now = LocalDateTime.now();
                LocalDateTime tomorrowMidnight = now.toLocalDate().plusDays(1).atStartOfDay();
                long secondsToMidnight = Duration.between(now, tomorrowMidnight).getSeconds();

                redisUtils.setTimeCache(key, userIndexSignVO, secondsToMidnight, TimeUnit.SECONDS);
                log.info("已经插入到缓存了");
                return true;
            } else {
                log.info("插入数据库失败");
                throw new RuntimeException("插入数据库失败");
            }
        } else {
            return true;
        }
    }

    // 根据用户的user_id来查询数据的情况
    @Override
    public List<UserIndexSignVO> getHistoryPoints(String userId) {
        log.info("进行逻辑层的开始");
        String key = GET_INDEX_HISTORY_POINTS + userId;
        Object jsonObject = redisUtils.getCache(key);
        if (jsonObject == null) {
            log.info("进行数据库的查询情况解决");
            List<UserIndexSignVO> lists = indexUserMapper.getHistoryPoints(userId);
            if (lists == null || lists.size() == 0) {
                log.info("数据库查询的数据不存在的");
                throw new RuntimeException("数据库出现了问题");
            }
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime tomorrowMidnight = now.toLocalDate().plusDays(1).atStartOfDay();
            long secondsToMidnight = Duration.between(now, tomorrowMidnight).getSeconds();
            redisUtils.setTimeCache(key, lists, secondsToMidnight, TimeUnit.SECONDS);

            log.info("进行返回给前端的数据");
            return lists;
        }
        log.info("查询了缓存的情况是有数据的");
        return (List<UserIndexSignVO>) jsonObject;
    }

    // 进行签到的时候能够修改数据积分
    @Override
    public UserMessageVO insertPoints(String userId) {
        try {
            Boolean flag = indexUserMapper.insertPoints(userId);
            if (flag) {
                String key = USER_ID + userId;
                redisUtils.deleteCache(key);
                Object object = redisUtils.queryWithSetCache(
                        userId,
                        USER_ID,
                        UserMessageVO.class,
                        (id) -> indexUserMapper.getByTotalNumberUser(id));

                if (object == null) {
                    // 缓存数据为空的处理
                }

                UserMessageVO result = null;
                if (object instanceof String) {
                    String jsonStr = (String) object;
                    if (StringUtils.isNotBlank(jsonStr)) {
                        result = JSONUtil.toBean(jsonStr, UserMessageVO.class);
                        return result;
                    }
                } else if (object instanceof UserMessageVO) {
                    result = (UserMessageVO) object;
                    return result;
                } else {
                    log.warn("未知的缓存数据类型 {}", object.getClass());
                    return null;
                }
            } else {
                log.warn("没有成功的插入数据库数据");
                return null;
            }
        } catch (Exception e) {
            log.info("出现了错误性的数据问题");
            throw new RuntimeException(e);
        }
        return null;
    }

    // 连续一周签到增加积分为20
    @Override
    public void insertPointsWeek(String userId) {
        if (userId == null) {
            log.info("接受层传递过来的数据");
            throw new RuntimeException("传递过来的数据不存在");
        }
        Boolean flag = indexUserMapper.insertPointsWeek(userId);
        if (flag) {
            log.info("新增积分成功");
        } else {
            log.info("新增失败");
            throw new RuntimeException("新增失败");
        }
    }

    // 连续签到一个月
    @Override
    public void insertPointsMonth(String userId) {
        if (userId == null) {
            log.info("传递过来的userId是不存在");
            throw new RuntimeException("传递过来的userId不存在");
        }
        Boolean flag = indexUserMapper.insertPointsMonth(userId);
        if (flag) {
            log.info("新增积分成功");
        } else {
            log.info("新增失败");
            throw new RuntimeException("新增失败");
        }
    }
}