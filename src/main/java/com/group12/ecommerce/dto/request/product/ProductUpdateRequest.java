package com.group12.ecommerce.dto.request.product;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductUpdateRequest {
    String name;
    String image;
    Double price;
    String description;
    Long total;
    Set<Long> categoryIds;
}
