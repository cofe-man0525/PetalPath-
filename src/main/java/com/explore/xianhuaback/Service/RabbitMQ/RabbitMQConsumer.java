package com.explore.xianhuaback.Service.RabbitMQ;


import com.explore.xianhuaback.DTO.AppointmentUserDTO.AppointmentUserDTO;
import com.explore.xianhuaback.DTO.UserOrderDTO.OrderPayUserDTO;
import com.explore.xianhuaback.Entity.OrderPayMessage;
import com.explore.xianhuaback.Service.user.Impl.RabbitMQServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class RabbitMQConsumer{

    @Autowired
    private RabbitMQServiceImpl rabbitMQServiceImpl;

    //websocket对象的情况配置的类情况
    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    /**
     * 监听队列，处理预约
     */
    @RabbitListener(queues="appointment.queue")
    public void processAppointment(AppointmentUserDTO appointmentUserDTO){
        log.info("消费者预约队列");

        try{
            Thread.sleep(2000);

            String result=rabbitMQServiceImpl.createAppointment(appointmentUserDTO);

            if(result.equals("预约成功")){

                log.info("插入成功");
                // 处理完成后，通过 WebSocket 通知前端
                messagingTemplate.convertAndSend(
                        "/topic/result/" + appointmentUserDTO.getUserId(),
                        result
                );
            }

        }catch (Exception e){
            log.info("预约失败");
            messagingTemplate.convertAndSend("/topic/result/" + appointmentUserDTO.getPhone());
        }
    }

    //订单的消费者的队列情况
    @RabbitListener(queues="payment.process.queue")
    public void processPayOrder(OrderPayMessage orderPayMessage){
        log.info("消费者订单队列的情况");
        try{
            Thread.sleep(2000);

            log.info("进行订单的处理情况");
            String result=rabbitMQServiceImpl.resultOrderPay(orderPayMessage);
            if(result.equals("支付完成")){
                log.info("订单的支付完整的情况");

                //回调对应的websocket的消息栏目的情况
                messagingTemplate.convertAndSend("/topic/payment/result"+orderPayMessage.getUserId()
                ,result);

            }
        }catch (Exception e){

            //支付失败的情况展示
            messagingTemplate.convertAndSend("/topic/payment/result"+orderPayMessage.getUserId());
            log.info("队列产生问题");  //放入到对饮过的死信队列
        }
    }

}
