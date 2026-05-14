package com.explore.xianhuaback.Controller.user;

import com.explore.xianhuaback.DTO.UserOrderDTO.PaymentRequestDTO;
import com.explore.xianhuaback.Entity.PaymentPayloadVO;
import com.explore.xianhuaback.Result.Result;
import com.explore.xianhuaback.Service.user.PaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/user")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/payLoad")
    public Result<PaymentPayloadVO> payLoad(@RequestBody PaymentRequestDTO paymentRequestDTO) {
        try {
            return Result.success(paymentService.createPayLoad(paymentRequestDTO));
        } catch (Exception e) {
            log.error("create payment payload failed", e);
            return Result.error(e.getMessage() == null ? "发起支付失败" : e.getMessage());
        }
    }
}
