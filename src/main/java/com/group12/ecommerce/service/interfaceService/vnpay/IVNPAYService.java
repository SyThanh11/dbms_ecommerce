package com.group12.ecommerce.service.interfaceService.vnpay;

import jakarta.servlet.http.HttpServletRequest;

import java.math.BigDecimal;

public interface IVNPAYService {
    String createPaymentUrl(BigDecimal total, String orderInfor, String urlReturn, HttpServletRequest request);
    int orderReturn(HttpServletRequest request);
}
