package com.websitePc.websidePc.repository;

import com.websitePc.websidePc.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    @Query(value = """
		SELECT
		    p.product_id,
            p.product_name,
            p.product_price,
		    op.quantity,
            p.product_type
        FROM
            product p
        JOIN
            order_product op on op.product_id = p.product_id
		WHERE
		    op.order_id = :orderId
        ORDER BY
            p.product_price desc
        """,
            nativeQuery = true)
    Page<Object[]> findProductByOrderId(String orderId, Pageable pageable);

    @Modifying
    @Query(value = """
        UPDATE product p
        SET p.product_in_stock = p.product_in_stock - :buyItem
        WHERE p.product_id = :productId
    """, nativeQuery = true)
    void updateInStockByProductId(Long productId, Integer buyItem);

    @Query(value = """
			SELECT
                 p.product_id,
                 p.product_name,
                 p.product_price,
			     p.product_type,
                 GROUP_CONCAT(i.img_link SEPARATOR ', ') AS product_img
            FROM product p
            LEFT JOIN img i ON p.product_id = i.product_id
            WHERE product_price BETWEEN :price - :priceRange AND :price + :priceRange
            AND p.product_id != :productId AND p.product_in_stock > 0 AND p.product_type = :type
            GROUP BY p.product_id
            LIMIT 6;
           """, nativeQuery = true)
    List<Object[]> recommendByPrice(
            Long productId,
            BigDecimal price,
            BigDecimal priceRange,
            String type
    );

    @Query(value = """
            SELECT
                p.product_id,
                p.product_name,
                p.product_price,
                GROUP_CONCAT(i.img_link SEPARATOR ', ') AS product_img,
                p.product_in_stock,
                p.product_type,
                (SELECT COUNT(*) FROM review r WHERE r.product_id = p.product_id) AS total_reviews,
                (SELECT ROUND(AVG(score), 1) FROM review r WHERE r.product_id = p.product_id) AS average_score
            FROM
                product p
            LEFT JOIN
                img i ON p.product_id = i.product_id
            WHERE
                p.product_id = :productId
            GROUP BY
                p.product_id;
            """, nativeQuery = true)
    Object findProductById(Long productId);

//    sản phẩm pc hoặc laptop mới nhất
    @Query(value = """
        SELECT
            p.product_id,
            p.product_name,
            p.product_price,
            GROUP_CONCAT(i.img_link SEPARATOR ', ') AS product_img,
            p.product_type
        FROM
            product p
        LEFT JOIN
            img i ON p.product_id = i.product_id
        WHERE p.product_type = :productType
        GROUP BY
            p.product_id,
            p.create_date
        ORDER BY
            p.create_date DESC, p.product_id ASC
        """,
            nativeQuery = true)
    Page<Object[]> listProductNew(
            Pageable pageable,
            String productType
    );

//    cấu hình theo vga hoặc cpu
//    Sử dụng CONCAT để tạo mẫu tìm kiếm động (thêm ký tự % vào trước và sau :componentName).
@Query(value = """
       SELECT
                  p.product_id,
                  p.product_name,
                  p.product_price,
                  GROUP_CONCAT(i.img_link SEPARATOR ", ") as img_link,
                  p.product_type
       FROM product p
       LEFT JOIN img i ON p.product_id = i.product_id
       WHERE p.product_id IN (
           SELECT pc.product_id
           FROM component c
           JOIN product_component pc ON c.component_id = pc.component_id
           WHERE c.component_type = :componentType AND c.component_name LIKE CONCAT('%', :componentName, '%')
       )
       GROUP BY p.product_id
       """,
        nativeQuery = true)
        Page<Object[]> categoryCpuVga(
                Pageable pageable,
                String componentType,
                String componentName
        );

    //    cấu hình theo vga hoặc cpu
//    Sử dụng CONCAT để tạo mẫu tìm kiếm động (thêm ký tự % vào trước và sau :componentName).
    @Query(value = """
       SELECT
            p.product_id, 
            p.product_name, 
            p.product_price, 
            GROUP_CONCAT(i.img_link SEPARATOR ", ") as img_link,
            p.product_type
       FROM product p
       LEFT JOIN img i ON p.product_id = i.product_id
       WHERE p.product_name LIKE CONCAT('%', :productName, '%')
       GROUP BY p.product_id
       """,
            nativeQuery = true)
    Page<Object[]> findProductByName(
            Pageable pageable,
            String productName
    );
}
