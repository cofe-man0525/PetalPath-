package com.explore.xianhuaback.Utils;

import com.explore.xianhuaback.Config.RedisConfig;
import com.explore.xianhuaback.Entity.utils.RedisData;
import io.micrometer.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Supplier;

import cn.hutool.json.JSONUtil;
@Slf4j
@Component

//缓存的解决方案的解决
public class RedisUtils {


    @Autowired
    private RedisConfig  redisConfig;

    //正常的数据情况
    private static final Long CACHE_NORMAL_TTL= 30L;

    //空数据的缓存时间
    private static final Long CACHE_NULL_TTL=5L;

    //1.针对于写入的操作
    public void setCache(String key , Object value, int time, TimeUnit timeUnit ){
        log.info("首先传入的是key,value,time,timeUnit");

        redisConfig.redisTemplate().opsForValue().set(key,value,time,timeUnit);
    }

    //写时间的单位控制时间的情况对应的
    public void setTimeCache(String key , Object value, long time, TimeUnit timeUnit){

        log.info("首先进行获取的对应数据情况");
        redisConfig.redisTemplate().opsForValue().set(key,value,time,timeUnit);
    }

    // 简单查询
    public Object getCache(String key) {
        log.info("查询缓存，key: {}", key);
        return redisConfig.redisTemplate().opsForValue().get(key);
    }

    //查询的操作(传递过来的参数进行逻辑写法)
    //无锁
    //解决普通的查询和普通业务（用户、订单）
    public <T, ID> T queryWithSetCache(ID id, String keyPrefix, Class<T> type, Function<ID, T> dbFallback) {
        String key = keyPrefix + id;

        // 1. 查缓存
        String json = (String) redisConfig.redisTemplate().opsForValue().get(key);

        // 2. 缓存命中 -> 正常数据
        if (StringUtils.isNotBlank(json)) {
            log.info("缓存命中正常数据: {}", key);
            return JSONUtil.toBean(json, type);
        }

        // 3. 缓存命中 -> 空值缓存（防穿透）
        if (json != null) {  // 此时 json 是空字符串
            log.info("缓存命中空值: {}", key);
            return null;
        }

        log.info("查询数据库开始");
        // 4. 缓存未命中 -> 查数据库
        log.info("缓存未命中，查询数据库: {}", key);
        T t = dbFallback.apply(id);
        //进行数据的缓存处理

        // 5. 回填缓存
        if (t == null) {
            // 空值缓存（短TTL，防穿透）
            redisConfig.redisTemplate().opsForValue().set(key, "", CACHE_NULL_TTL, TimeUnit.MINUTES);
            return null;
        }

             // 正常数据缓存（可以加随机TTL防雪崩）
        redisConfig.redisTemplate().opsForValue().set(key, JSONUtil.toJsonStr(t), CACHE_NORMAL_TTL, TimeUnit.MINUTES);
        return t;
    }


    //解决缓存击穿问题（应对于中低并发时候）
    //高并发热点数据（首页、秒杀）
    //采用的是互斥锁
    public <T,ID> T queryWithMutexLock(ID id, String keyPrefix,Class<T> type, Function<ID,T>  dbFallback){
        String key=keyPrefix+id;
        String lockKey = key + ":lock";

        if(key==null){
            log.info("传递过来的关键id不存在");
            throw new RuntimeException("传递过来的关键id");
        }

        //设置互斥锁
        String withLock=key+"lock";

        //查询缓存

        String json=(String) redisConfig.redisTemplate().opsForValue().get(key);

        //缓存命中的时候缓存存在
        if(json!=null && json.isEmpty()){
            log.info("缓存命中了但是数据是空的");
            return null;
        }

        //缓存存在就是正常的返回数据
        if(StringUtils.isNotBlank(json)){
            log.info("返回正常数据");
            return  JSONUtil.toBean(json,type);
        }

        //缓存未命中开始获得锁来进行修改缓存
        Boolean locked = redisConfig.redisTemplate().opsForValue()
                .setIfAbsent(lockKey, "1", 10, TimeUnit.SECONDS); // 锁超时10秒
        log.info("进行数据的传递情况");
        if (Boolean.TRUE.equals(locked)) {
            try {
                // 双重检查
                json = (String) redisConfig.redisTemplate().opsForValue().get(key);
                if (StringUtils.isNotBlank(json)) {
                    return JSONUtil.toBean(json, type);
                }
                if (json != null) return null;

                // 3. 查询数据库
                T result = dbFallback.apply(id);

                // 4. 回填缓存（防穿透 + 防雪崩）
                if (result == null) {
                    redisConfig.redisTemplate().opsForValue().set(key, "", CACHE_NULL_TTL, TimeUnit.MINUTES);
                    return null;
                }
                // 正常数据：随机TTL防雪崩
                long randomTtl = CACHE_NORMAL_TTL * 60 + new Random().nextInt(300); // 30分钟转秒 + 0~300秒
                String jsonResult = JSONUtil.toJsonStr(result);
                redisConfig.redisTemplate().opsForValue().set(key, jsonResult, randomTtl, TimeUnit.SECONDS);
                log.info("写入缓存: {}, TTL={}秒", key, randomTtl);
                return result;
            } finally {
                redisConfig.redisTemplate().delete(lockKey);
            }
        }
        // 5. 未获得锁：等待重试（简单自旋，适合中低并发）
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return queryWithMutexLock(id, keyPrefix, type, dbFallback);
    }

    //缓存的情况进行解析（数组的情况）
    public <T> List<T> queryListWithSetCache(String cacheKey, Class<T> elementType, Supplier<List<T>> dbFallback) {
        // 1. 查缓存
        String json = (String) redisConfig.redisTemplate().opsForValue().get(cacheKey);

        // 2. 缓存命中 -> 正常数据
        if (StringUtils.isNotBlank(json)) {
            log.info("缓存命中正常数据: {}", cacheKey);
            // 将JSON数组转换为List
            return JSONUtil.toList(json, elementType);
        }

        // 3. 缓存命中 -> 空值缓存
        if (json != null) {
            log.info("缓存命中空值: {}", cacheKey);
            return null;
        }

        // 4. 缓存未命中 -> 查数据库
        log.info("缓存未命中，查询数据库: {}", cacheKey);
        List<T> list = dbFallback.get();

        // 5. 回填缓存
        if (list == null || list.isEmpty()) {
            // 空值缓存
            redisConfig.redisTemplate().opsForValue().set(cacheKey, "", CACHE_NULL_TTL, TimeUnit.MINUTES);
            return null;
        }

        // 正常数据缓存
        redisConfig.redisTemplate().opsForValue().set(cacheKey, JSONUtil.toJsonStr(list), CACHE_NORMAL_TTL, TimeUnit.MINUTES);
        return list;
    }


    public boolean hasKey(String cacheKey) {

        return redisConfig.redisTemplate().hasKey(cacheKey);
    }

    public void deleteCache(String key) {
        redisConfig.redisTemplate().delete(key);
    }


}
