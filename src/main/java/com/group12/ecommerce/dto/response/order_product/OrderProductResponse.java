package com.group12.ecommerce.dto.response.order_product;

import com.group12.ecommerce.dto.response.product.ProductResponse;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderProductResponse {
    ProductResponse product;
    int quantity;
    BigDecimal priceAtPurchase;
}
