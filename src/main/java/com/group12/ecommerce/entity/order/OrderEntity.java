package com.group12.ecommerce.entity.order;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.group12.ecommerce.entity.order_product.OrderProductEntity;
import com.group12.ecommerce.entity.user.UserEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "`order`",
    indexes = {
            @Index(name = "idx_user_date_status", columnList = "user_id, date, status")
    }
)
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @Column(nullable = false)
    BigDecimal totalPrice;

    @Column(nullable = false)
    LocalDate date;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    OrderStatus status;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    Set<OrderProductEntity> orderProducts;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    UserEntity user;


}
