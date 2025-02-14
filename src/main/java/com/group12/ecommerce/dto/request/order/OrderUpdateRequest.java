package com.group12.ecommerce.dto.request.order;

import com.group12.ecommerce.dto.request.order_product.OrderProductDto;
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
public class OrderUpdateRequest {
    LocalDate date;
    Set<OrderProductDto> products;
}

