package com.explore.xianhuaback.Service.payment;

import com.explore.xianhuaback.DTO.UserOrderDTO.PaymentRequestDTO;
import com.explore.xianhuaback.Entity.OrderVO;
import com.explore.xianhuaback.Entity.PaymentPayloadVO;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class AlipayPaymentChannelClient implements PaymentChannelClient {

    @Override
    public boolean supports(String paymentMethod) {
        return "ALIPAY".equalsIgnoreCase(paymentMethod);
    }

    @Override
    public PaymentPayloadVO unifiedOrder(OrderVO orderVO, PaymentRequestDTO paymentRequestDTO) {
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("tradeNo", "mock_alipay_trade_" + orderVO.getOrderNo());
        params.put("orderNo", orderVO.getOrderNo());
        params.put("amount", orderVO.getPayAmount());
        params.put("subject", paymentRequestDTO.getSubject() == null ? "鲜花订单支付" : paymentRequestDTO.getSubject());
        params.put("formBody", "<form>mock alipay form body</form>");
        params.put("mock", true);

        PaymentPayloadVO payloadVO = new PaymentPayloadVO();
        payloadVO.setOrderNo(orderVO.getOrderNo());
        payloadVO.setPayAmount(orderVO.getPayAmount());
        payloadVO.setPaymentMethod("ALIPAY");
        payloadVO.setTradeType("PAGE_PAY");
        payloadVO.setChannelTradeNo((String) params.get("tradeNo"));
        payloadVO.setPayParams(params);
        return payloadVO;
    }
}
