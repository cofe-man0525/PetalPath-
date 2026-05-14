package com.explore.xianhuaback.Config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // 队列名称
    public static final String APPOINTMENT_QUEUE = "appointment.queue";
    public static final String APPOINTMENT_RESULT_QUEUE = "appointment.result.queue";

    // 预约的交换机
    public static final String APPOINTMENT_EXCHANGE = "appointment.exchange";



    // ========== 支付普通队列 ==========  支付的队列和交换机
    public static final String PAYMENT_QUEUE = "payment.process.queue";
    public static final String PAYMENT_EXCHANGE = "payment.exchange";

    // ========== 延迟队列（30分钟超时检查）==========  //延迟队列的和交换机
    public static final String DELAY_QUEUE = "order.delay.queue";
    public static final String DELAY_EXCHANGE = "order.exchange";

    @Bean
    public Queue appointmentQueue() {
        // 持久化队列，防止重启丢失
        return QueueBuilder.durable(APPOINTMENT_QUEUE).build();
    }

    @Bean
    public Queue appointmentResultQueue() {
        return QueueBuilder.durable(APPOINTMENT_RESULT_QUEUE).build();
    }

    @Bean
    public DirectExchange appointmentExchange() {
        return new DirectExchange(APPOINTMENT_EXCHANGE);
    }

    @Bean
    public Binding appointmentBinding() {
        return BindingBuilder
                .bind(appointmentQueue())
                .to(appointmentExchange())
                .with("appointment.create");
    }

    @Bean
    public Binding appointmentResultBinding() {
        return BindingBuilder
                .bind(appointmentResultQueue())
                .to(appointmentExchange())
                .with("appointment.result");
    }


    /**
     * 支付普通队列（持久化）
     */
    @Bean
    public Queue paymentQueue() {
        return QueueBuilder.durable(PAYMENT_QUEUE).build();
    }

    /**
     * 支付直连交换机
     */
    @Bean
    public DirectExchange paymentExchange() {
        return new DirectExchange(PAYMENT_EXCHANGE);
    }

    /**
     * 绑定支付队列到支付交换机，routingKey = "payment.process"
     */
    @Bean
    public Binding paymentBinding() {
        return BindingBuilder.bind(paymentQueue())
                .to(paymentExchange())
                .with("payment.process");
    }

    /**
     * 延迟队列（需要安装 rabbitmq_delayed_message_exchange 插件）
     */
    @Bean
    public Queue delayQueue() {
        return QueueBuilder.durable(DELAY_QUEUE).build();
    }

    /**
     * 延迟消息交换机（x-delayed-message 类型）
     */
    @Bean
    public CustomExchange delayExchange() {
        java.util.Map<String, Object> args = new java.util.HashMap<>();
        args.put("x-delayed-type", "direct");
        return new CustomExchange(DELAY_EXCHANGE, "x-delayed-message", true, false, args);
    }

    /**
     * 绑定延迟队列到延迟交换机，routingKey = "order.delay"
     */
    @Bean
    public Binding delayBinding() {
        return BindingBuilder.bind(delayQueue())
                .to(delayExchange())
                .with("order.delay")
                .noargs();
    }
}
