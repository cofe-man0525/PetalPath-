package com.explore.xianhuaback.Service.user.Impl;

import com.explore.xianhuaback.DTO.UserOrderDTO.PaymentRequestDTO;
import com.explore.xianhuaback.Entity.OrderVO;
import com.explore.xianhuaback.Entity.PaymentPayloadVO;
import com.explore.xianhuaback.Mapper.user.OrderPayUserMapper;
import com.explore.xianhuaback.Service.payment.PaymentChannelClient;
import com.explore.xianhuaback.Service.user.PaymentService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    private static final int WAITING_PAY_STATUS = 0;

    private final RedissonClient redissonClient;
    private final OrderPayUserMapper orderPayUserMapper;
    private final List<PaymentChannelClient> paymentChannelClients;

    public PaymentServiceImpl(RedissonClient redissonClient,
                              OrderPayUserMapper orderPayUserMapper,
                              List<PaymentChannelClient> paymentChannelClients) {
        this.redissonClient = redissonClient;
        this.orderPayUserMapper = orderPayUserMapper;
        this.paymentChannelClients = paymentChannelClients;
    }

    @Override
    public PaymentPayloadVO createPayLoad(PaymentRequestDTO paymentRequestDTO) {
        validateRequest(paymentRequestDTO);

        String orderNo = paymentRequestDTO.getOrderNo();
        RLock lock = redissonClient.getLock("lock:payment:payload:" + orderNo);
        boolean locked = false;
        try {
            locked = lock.tryLock(3, 15, TimeUnit.SECONDS);
            if (!locked) {
                throw new RuntimeException("系统繁忙，请稍后再试");
            }

            OrderVO orderVO = orderPayUserMapper.getByOrderNo(orderNo);
            if (orderVO == null) {
                throw new RuntimeException("订单不存在");
            }
            if (!Integer.valueOf(WAITING_PAY_STATUS).equals(orderVO.getPayStatus())) {
                throw new RuntimeException("订单不是待支付状态，不能重复发起支付");
            }
            if (paymentRequestDTO.getPayAmount().compareTo(orderVO.getPayAmount()) != 0) {
                throw new RuntimeException("支付金额与订单金额不一致");
            }

            String paymentMethod = normalizeMethod(paymentRequestDTO.getPaymentMethod());
            return paymentChannelClients.stream()
                    .filter(client -> client.supports(paymentMethod))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("暂不支持的支付方式"))
                    .unifiedOrder(orderVO, paymentRequestDTO);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("支付请求被中断");
        } finally {
            if (locked && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    private void validateRequest(PaymentRequestDTO requestDTO) {
        if (requestDTO == null) {
            throw new RuntimeException("支付参数不能为空");
        }
        if (!StringUtils.hasText(requestDTO.getOrderNo())) {
            throw new RuntimeException("订单号不能为空");
        }
        BigDecimal payAmount = requestDTO.getPayAmount();
        if (payAmount == null || payAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("支付金额必须大于0");
        }
    }

    private String normalizeMethod(String paymentMethod) {
        if (!StringUtils.hasText(paymentMethod)) {
            return "WECHAT";
        }
        return paymentMethod.trim().toUpperCase();
    }
}
