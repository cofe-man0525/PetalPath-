package com.explore.xianhuaback.Service.user.Impl;

import com.explore.xianhuaback.DTO.AppointmentUserDTO.AppointmentUserDTO;
import com.explore.xianhuaback.Mapper.user.AppointmentUserMapper;
import com.explore.xianhuaback.Service.user.AppointmentUserService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBlockingDeque;
import org.redisson.api.RSet;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AppointmentServiceImpl implements AppointmentUserService {

    //消息队列的区分名字展示
    private final static String QUEUE_PREFIX  = "appointment:waiting";

    //导入对应的redisson的放置到对象容器中
    @Autowired
    private RedissonClient redissonClient;

    //对应的消息队列
    @Autowired
    private RabbitTemplate rabbitTemplate;

    //websocket
    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private RedisTemplate redisTemplate;

    //预约的消息队列情况
    @Override
    public String joinWaitingQueue(AppointmentUserDTO appointmentUserDTO) {

        log.info("数据的解构");
        String data=appointmentUserDTO.getDate();   //对应的数据情况的展示

        String queueKey= QUEUE_PREFIX+data;  //消息队列键

        String userKey=appointmentUserDTO.getPhone()+queueKey;

        //这里的是存放对应的（这里就是存放当天在排队的用户的手机和信息）   设置出对应的天的消息排队情况
        RSet<String> waitingUsers=redissonClient.getSet(queueKey);

        //判断对应的用户是否在排队中如果存在就返回对应的信息内容
        if(waitingUsers.contains(userKey)){
            log.info("该用户在进行排队中");

            return "你还在排队中";
        }

        //放入将缓存中放入其中的情况
        Long currentCount=redisTemplate.opsForValue().increment(queueKey,1);


        if(currentCount>=10){
            log.info("当天的预约时间已经满了");
            //进行回滚的操作

            redisTemplate.opsForValue().decrement(queueKey);

            return "当天预约已经满了";
        }
        //如果没有将放入到阻塞队列中
        RBlockingDeque<AppointmentUserDTO> queue=redissonClient.getBlockingDeque(queueKey);


        // 3. 限制最大排队人数（防止无限堆积）
        if (queue.size() >= 1000) {
            return "队伍已经满了，请稍后重试";
        }

        //加入成功后那就加入队列的尾部来处理进行排队
        queue.offer(appointmentUserDTO);
        waitingUsers.add(String.valueOf(appointmentUserDTO.getPhone()));

        // 6. 发送到 RabbitMQ，让消费者异步处理
        rabbitTemplate.convertAndSend(
                "appointment.exchange",
                "appointment.create",
                appointmentUserDTO
        );

        //这里的是根据websocket来通知给前端的情况
        return "已经加入排队中，请等待通知";
    }
}
