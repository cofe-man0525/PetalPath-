package com.explore.xianhuaback.Service.payment;

import com.explore.xianhuaback.DTO.UserOrderDTO.PaymentRequestDTO;
import com.explore.xianhuaback.Entity.OrderVO;
import com.explore.xianhuaback.Entity.PaymentPayloadVO;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@Component
public class WechatPaymentChannelClient implements PaymentChannelClient {

    private static final String PLACEHOLDER_APP_ID = "wx-placeholder-appid";
    private static final String PLACEHOLDER_MCH_ID = "mch-placeholder";

    @Override
    public boolean supports(String paymentMethod) {
        return paymentMethod == null
                || paymentMethod.isBlank()
                || "WECHAT".equalsIgnoreCase(paymentMethod)
                || "WX".equalsIgnoreCase(paymentMethod);
    }

    @Override
    public PaymentPayloadVO unifiedOrder(OrderVO orderVO, PaymentRequestDTO paymentRequestDTO) {
        String nonceStr = UUID.randomUUID().toString().replace("-", "");
        String timeStamp = String.valueOf(Instant.now().getEpochSecond());
        String prepayId = "mock_prepay_" + orderVO.getOrderNo();

        Map<String, Object> params = new LinkedHashMap<>();
        params.put("appId", PLACEHOLDER_APP_ID);
        params.put("mchId", PLACEHOLDER_MCH_ID);
        params.put("timeStamp", timeStamp);
        params.put("nonceStr", nonceStr);
        params.put("package", "prepay_id=" + prepayId);
        params.put("signType", "RSA");
        params.put("paySign", "mock-pay-sign-" + nonceStr);
        params.put("mock", true);

        PaymentPayloadVO payloadVO = new PaymentPayloadVO();
        payloadVO.setOrderNo(orderVO.getOrderNo());
        payloadVO.setPayAmount(orderVO.getPayAmount());
        payloadVO.setPaymentMethod("WECHAT");
        payloadVO.setTradeType(paymentRequestDTO.getOpenId() == null || paymentRequestDTO.getOpenId().isBlank() ? "NATIVE" : "JSAPI");
        payloadVO.setChannelTradeNo(prepayId);
        payloadVO.setPayParams(params);
        return payloadVO;
    }
}
