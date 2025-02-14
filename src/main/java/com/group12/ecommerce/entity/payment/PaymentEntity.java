package com.group12.ecommerce.entity.payment;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.group12.ecommerce.entity.order.OrderEntity;
import com.group12.ecommerce.entity.user.UserEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "payment")
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaymentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    UserEntity user;

    @OneToOne
    @JoinColumn(name = "order_id", nullable = false, unique = true)
    @JsonIgnore
    OrderEntity order;

    @Column(nullable = false)
    String method;  // PAYPAL, CREDIT_CARD, BANK_TRANSFER

    @Column(nullable = false)
    LocalDateTime date;

    @Column(nullable = false)
    BigDecimal pricePayment;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    PaymentStatus status; // NEW, PROCESSING, SUCCESS, FAILED
}
