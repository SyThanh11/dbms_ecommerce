package com.group12.ecommerce.dto.response.product;

import com.group12.ecommerce.entity.category.CategoryEntity;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductResponse {
    Long id;
    String name;
    String image;
    Double price;
    String description;
    Long total;
    Set<CategoryEntity> categories;
}
