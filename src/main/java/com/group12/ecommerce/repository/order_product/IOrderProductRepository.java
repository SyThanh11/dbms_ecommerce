package com.group12.ecommerce.repository.order_product;

import com.group12.ecommerce.entity.order.OrderEntity;
import com.group12.ecommerce.entity.order_product.OrderProductEntity;
import com.group12.ecommerce.entity.product.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.Set;

public interface IOrderProductRepository extends JpaRepository<OrderProductEntity, Long> {
    Optional<OrderProductEntity> findByOrderAndProduct(OrderEntity order, ProductEntity product);

    @Modifying
    @Query("DELETE FROM OrderProductEntity op WHERE op.order = :order AND op NOT IN :newOrderProducts")
    void deleteByOrderAndNotIn(@Param("order") OrderEntity order, @Param("newOrderProducts") Set<OrderProductEntity> newOrderProducts);
}
