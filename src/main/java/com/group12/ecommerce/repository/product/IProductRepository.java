package com.group12.ecommerce.repository.product;

import com.group12.ecommerce.entity.product.ProductEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IProductRepository extends JpaRepository<ProductEntity, Long>,
        JpaSpecificationExecutor<ProductEntity> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM ProductEntity p WHERE p.id = :id")
    Optional<ProductEntity> findByIdForUpdate(@Param("id") Long id);

    // Truy vấn tìm sản phẩm có giá trong khoảng và sắp xếp theo doanh số
    /*
    *  SELECT p.*,
       COALESCE(SUM(op.quantity), 0) AS totalSold
       FROM product p
       LEFT JOIN order_product op ON p.id = op.product_id
       GROUP BY p.id
       HAVING p.price BETWEEN :minPrice AND :maxPrice
       ORDER BY totalSold DESC
       LIMIT 20 OFFSET :offset;
    * */
    /*
    * Tối ưu:
    *   Dùng HAVING p.price BETWEEN :minPrice AND :maxPrice sau khi nhóm dữ liệu, tránh lọc trước rồi nhóm lại gây tốn tài nguyên.
        Index trên price (idx_product_price) giúp tối ưu tìm kiếm sản phẩm theo giá.
        Index trên product_id trong order_product giúp tăng tốc truy vấn JOIN.
    * */
    @Query("SELECT p FROM ProductEntity p " +
            "LEFT JOIN OrderProductEntity op ON p.id = op.product.id " +
            "GROUP BY p.id " +
            "HAVING p.price BETWEEN :minPrice AND :maxPrice " +
            "ORDER BY COUNT(op.quantity) DESC")
    List<ProductEntity> findBestSellingProducts(@Param("minPrice") Double minPrice,
                                                @Param("maxPrice") Double maxPrice,
                                                Pageable pageable);

    @Query("SELECT p FROM ProductEntity p " +
            "JOIN p.categories c " +
            "WHERE (:name IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))) " +
            "AND (:minPrice IS NULL OR p.price >= :minPrice) " +
            "AND (:maxPrice IS NULL OR p.price <= :maxPrice) " +
            "AND (:categoryId IS NULL OR c.id = :categoryId)")
    Page<ProductEntity> searchProducts(
            String name,
            Double minPrice,
            Double maxPrice,
            Long categoryId,
            Pageable pageable
    );
}
