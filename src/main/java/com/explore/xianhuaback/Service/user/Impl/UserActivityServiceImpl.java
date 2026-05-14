package com.explore.xianhuaback.Service.user.Impl;

import com.explore.xianhuaback.Entity.ActivityUser.ActivityUser;
import com.explore.xianhuaback.Entity.ActivityUser.ActivityUserVO;
import com.explore.xianhuaback.Entity.ActivityUser.GetActivityUserVO;
import com.explore.xianhuaback.Entity.AdminAddActivity.ActivityVO;
import com.explore.xianhuaback.Mapper.user.UserActivityMapper;
import com.explore.xianhuaback.Service.user.ActivityService;
import com.explore.xianhuaback.Utils.RedisUtils;
import com.qiniu.util.Json;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class UserActivityServiceImpl implements ActivityService {

    //将对应的数据进行保存在缓存中
    private static final String ADMIN_ACTIVITY="admin_activity";

    //用户的id情况
    private static final String USER_ACTIVITY_ID="user_activity_id:";

    //用户个人的数据情况来
    private static final String USER_ACTIVITY="user_activity:";
    @Autowired
    private UserActivityMapper activityMapper;

    @Autowired
    private RedisUtils redisUtils;

    //获得活动列表情况
    @Override
    public List<ActivityUserVO> getActivityList() {
            //首先进行查询缓存的数据情况
        Object object =redisUtils.getCache(ADMIN_ACTIVITY);
        if(object!=null){
            List<ActivityUserVO> list=(List<ActivityUserVO>)object;
            return list;
        }
            List<ActivityUserVO> lists=activityMapper.getActivity();
            if(lists==null||lists.size()==0){
                log.info("从数据库中找到的数据不存在");
                return null;
            }else{
                //首先将数据进行缓存到缓存中
                redisUtils.setCache(ADMIN_ACTIVITY,lists,10, TimeUnit.MINUTES);
                return lists;
            }
    }

    //跟据用户的userId来查询用户的数据情况
    @Override
    public List<GetActivityUserVO> getUserActivity(String userId) {
        if(userId==null){
            log.info("接受层传递的用户id不存在");
            throw new RuntimeException("在ServiceImpl中出现了错误");
        }

        //首先在缓存中查询数据的情况
        Object object =redisUtils.getCache(USER_ACTIVITY_ID);
        if(object!=null){
            List<GetActivityUserVO> list=(List<GetActivityUserVO>)object;
            return list;
        }

        List<GetActivityUserVO> lists=activityMapper.getUserActivity(userId);
        if(lists==null||lists.size()==0){
            log.info("从数据库中找到的数据不存在");
            return null;
        }else{

            //首先将数据进行缓存到缓存中
            redisUtils.setCache(USER_ACTIVITY_ID,lists,10, TimeUnit.MINUTES);
            return lists;
        }
    }


    @Override
    public Boolean addUserActivity(String userId, String activityId) {
        if(userId==null){
            log.info("传递的过来的用户id或者是活动id不存在");
            throw new RuntimeException("出现了错误查看前端的情况");
        }

        //查询出来的数据情况来展示出来
        String key=USER_ACTIVITY+userId;
        Object json=redisUtils.getCache(key);
        if(json==null){

            //从数据库中查询数据再次进行返回给前端的情况()
            ActivityVO activityVO=activityMapper.getByActivityId(activityId);
            if(activityVO==null){
                log.info("传递的过来的活动id不存在情况");
                throw new RuntimeException("进行用户传递的数据库数据不存在");
            }

            //将数据进行缓存到redis中
            redisUtils.setCache(key,activityVO,15, TimeUnit.DAYS);
            //将这个数据进行插入到对应的数据库方面

            ActivityUser activityUser=new ActivityUser();

            activityUser.setActivityId(activityId);
            activityUser.setUserId(userId);

             BeanUtils.copyProperties(activityVO,activityUser);
            Boolean flag=activityMapper.insertUserActivity(activityUser);
            if(flag){
                log.info("插入成功");
            }else{
                log.info("插入失败");
                throw new RuntimeException("查看一下数据层面把插入失败");
            }
        }
        //如果缓存存在的情况就进行告诉前端这个活动已经报名过了，出去这个后时间过期了们就不能接着·报名了
        return false;
    }
}
