package com.explore.xianhuaback.Service.user;

import com.explore.xianhuaback.DTO.UserOrderDTO.PaymentRequestDTO;
import com.explore.xianhuaback.Entity.PaymentPayloadVO;

public interface PaymentService {
    PaymentPayloadVO createPayLoad(PaymentRequestDTO paymentRequestDTO);
}
