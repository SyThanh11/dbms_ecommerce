package com.group12.ecommerce.entity.category;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.group12.ecommerce.entity.product.ProductEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "category")
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CategoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(columnDefinition = "varchar(255) comment 'name'", nullable = false, unique = true)
    String name;

    @Column(columnDefinition = "varchar(255) comment 'description'")
    String description;

    @ManyToMany(mappedBy = "categories", fetch = FetchType.LAZY)
    @JsonIgnore
    Set<ProductEntity> products;
}
