package com.group12.ecommerce.entity.order_product;

import com.group12.ecommerce.entity.order.OrderEntity;
import com.group12.ecommerce.entity.product.ProductEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(
        name = "order_product",
        indexes = {
                @Index(name = "idx_product", columnList = "product_id"),
                @Index(name = "idx_order", columnList = "order_id")
        })
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderProductEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    OrderEntity order;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    ProductEntity product;

    @Column(nullable = false)
    int quantity;

    @Column(nullable = false)
    BigDecimal priceAtPurchase;
}
