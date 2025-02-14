package com.group12.ecommerce.repository.payment;

import com.group12.ecommerce.entity.payment.PaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IPaymentRepository extends JpaRepository<PaymentEntity, String> {
    Optional<PaymentEntity> findByOrderId(String orderId);
}