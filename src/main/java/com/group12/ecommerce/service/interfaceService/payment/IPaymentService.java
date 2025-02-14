package com.group12.ecommerce.service.interfaceService.payment;

import com.group12.ecommerce.entity.order.OrderEntity;
import com.group12.ecommerce.entity.payment.PaymentEntity;

public interface IPaymentService {
    PaymentEntity processPayment(OrderEntity order, String paymentMethod);
    boolean processPayPalPayment(OrderEntity order);
}
