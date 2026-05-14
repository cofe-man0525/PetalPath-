package com.explore.xianhuaback.Entity;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Map;

@Data
public class PaymentPayloadVO implements Serializable {

    private String orderNo;

    private BigDecimal payAmount;

    private String paymentMethod;

    private String tradeType;

    private String channelTradeNo;

    private Map<String, Object> payParams;
}
