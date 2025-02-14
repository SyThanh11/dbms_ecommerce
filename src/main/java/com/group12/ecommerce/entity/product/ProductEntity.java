package com.group12.ecommerce.entity.product;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.group12.ecommerce.entity.category.CategoryEntity;
import com.group12.ecommerce.entity.order.OrderEntity;
import com.group12.ecommerce.entity.order_product.OrderProductEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "product")
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(columnDefinition = "varchar(255) comment 'name'", nullable = false, unique = true)
    String name;

    @Column(columnDefinition = "varchar(255) comment 'description'")
    String description;

    @Column(columnDefinition = "varchar(255) comment 'image'")
    String image;

    @Column(nullable = false)
    Double price;

    @Column(name = "total", nullable = false)
    Long total;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(
            name = "product-category",
            joinColumns = @JoinColumn(name = "product_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "category_id", referencedColumnName = "id")
    )
    @JsonIgnore
    Set<CategoryEntity> categories;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    Set<OrderProductEntity> orderProducts;
}
