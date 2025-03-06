package com.group12.ecommerce.repository.order;

import com.group12.ecommerce.entity.order.OrderEntity;
import com.group12.ecommerce.entity.order.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface IOrderRepository extends JpaRepository<OrderEntity, String> {
    // Truy vấn lịch sử mua hàng của người dùng
    /*
        SELECT o.id, o.date, o.total_price, COUNT(op.product_id) AS total_items
        FROM `order` o
        JOIN order_product op ON o.id = op.order_id
        WHERE o.user_id = :userId
        GROUP BY o.id
        ORDER BY o.date DESC
        LIMIT 10;
    */
    @Query("SELECT o FROM OrderEntity o " +
            "LEFT JOIN OrderProductEntity op ON o.id = op.order.id " +
            "WHERE o.user.id = :userId " +
            "AND (:status IS NULL OR o.status = :status) " +
            "GROUP BY o.id " +
            "ORDER BY o.date DESC")
    Page<OrderEntity> findUserOrderHistory(
            @Param("userId") String userId,
            @Param("status") OrderStatus status,
            Pageable pageable
    );

}
