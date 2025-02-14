package com.group12.ecommerce.repository.order;

import com.group12.ecommerce.entity.order.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IOrderRepository extends JpaRepository<OrderEntity, String> {
}
