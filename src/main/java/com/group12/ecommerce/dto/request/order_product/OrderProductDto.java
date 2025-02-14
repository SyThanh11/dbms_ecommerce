package com.group12.ecommerce.dto.request.order_product;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderProductDto {
    private Long productId;
    private int quantity;
    private BigDecimal priceAtPurchase;
}
