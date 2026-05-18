package com.ecommerce.product.repo;

import com.ecommerce.product.domain.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query(
            """
            SELECT p FROM Product p
            WHERE (:categoryId IS NULL OR p.category.id = :categoryId)
              AND (:q IS NULL OR :q = '' OR LOWER(p.name) LIKE LOWER(CONCAT('%', :q, '%'))
                   OR LOWER(p.description) LIKE LOWER(CONCAT('%', :q, '%')))
            """)
    Page<Product> search(
            @Param("categoryId") Long categoryId, @Param("q") String q, Pageable pageable);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(
            "UPDATE Product p SET p.stockQuantity = p.stockQuantity - :qty "
                    + "WHERE p.id = :id AND p.stockQuantity >= :qty")
    int decrementStockIfEnough(@Param("id") Long id, @Param("qty") int qty);
}
