package com.group12.ecommerce.dto.response.category;

import com.group12.ecommerce.entity.product.ProductEntity;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CategoryResponse {
    Long id;
    String name;
    String description;
    Set<ProductEntity> products;
}
