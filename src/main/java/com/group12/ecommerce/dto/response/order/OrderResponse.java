package com.group12.ecommerce.dto.response.order;

import com.group12.ecommerce.dto.request.order_product.OrderProductDto;
import com.group12.ecommerce.dto.response.order_product.OrderProductResponse;
import com.group12.ecommerce.entity.order.OrderStatus;
import com.group12.ecommerce.entity.product.ProductEntity;
import com.group12.ecommerce.entity.user.UserEntity;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderResponse {
    String id;
    LocalDate date;
    BigDecimal totalPrice;
    OrderStatus status;
    Set<OrderProductResponse> products;
    UserEntity user;
}
