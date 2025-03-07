package com.group12.ecommerce.service.implementService.payment;

import com.group12.ecommerce.entity.order.OrderEntity;
import com.group12.ecommerce.entity.payment.PaymentEntity;
import com.group12.ecommerce.entity.payment.PaymentStatus;
import com.group12.ecommerce.repository.payment.IPaymentRepository;
import com.group12.ecommerce.service.interfaceService.payment.IPaymentService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PaymentService implements IPaymentService {

    IPaymentRepository paymentRepository;

    @Override
    @PreAuthorize("hasAuthority('ROLE_ADMIN') || hasAuthority('ROLE_USER')")
    public PaymentEntity processPayment(OrderEntity order, String paymentMethod) {
        PaymentEntity payment = PaymentEntity.builder()
                .order(order)
                .user(order.getUser())
                .method(paymentMethod)
                .date(LocalDateTime.now())
                .pricePayment(order.getTotalPrice())
                .status(PaymentStatus.PROCESSING) // Ban đầu đặt trạng thái là PROCESSING
                .build();

        paymentRepository.save(payment);

        boolean success = processPayPalPayment(order);

        if (success) {
            payment.setStatus(PaymentStatus.SUCCESS);
            paymentRepository.save(payment);
            return payment;
        } else {
            payment.setStatus(PaymentStatus.FAILED);
            paymentRepository.save(payment);
            throw new RuntimeException("Payment failed, rolling back...");
        }
    }

    @Override
    @PreAuthorize("hasAuthority('ROLE_ADMIN') || hasAuthority('ROLE_USER')")
    public boolean processPayPalPayment(OrderEntity order) {
        log.info("Connecting to PayPal API...");
        try {
            Thread.sleep(2000); // Giả lập xử lý
        } catch (InterruptedException e) {
            throw new RuntimeException("Payment interrupted");
        }
        boolean success = Math.random() > 0.2; // 80% thành công
        log.info("PayPal payment status: {}", success ? "SUCCESS" : "FAILED");
        return success;
    }
}
