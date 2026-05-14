package com.explore.xianhuaback.DTO.UserOrderDTO;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class PaymentRequestDTO implements Serializable {

    private String orderNo;

    private BigDecimal payAmount;

    /**
     * WECHAT or ALIPAY. Defaults to WECHAT in service layer.
     */
    private String paymentMethod;

    /**
     * WeChat JSAPI payment needs openId. It is optional for current mock payload.
     */
    private String openId;

    private String clientIp;

    private String subject;
}
