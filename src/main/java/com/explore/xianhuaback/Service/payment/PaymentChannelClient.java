package com.explore.xianhuaback.Service.payment;

import com.explore.xianhuaback.DTO.UserOrderDTO.PaymentRequestDTO;
import com.explore.xianhuaback.Entity.OrderVO;
import com.explore.xianhuaback.Entity.PaymentPayloadVO;

public interface PaymentChannelClient {
    boolean supports(String paymentMethod);

    PaymentPayloadVO unifiedOrder(OrderVO orderVO, PaymentRequestDTO paymentRequestDTO);
}
