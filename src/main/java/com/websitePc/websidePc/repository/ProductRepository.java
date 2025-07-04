package com.websitePc.websidePc.repository;

import com.websitePc.websidePc.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
//    sản phẩm pc hoặc laptop mới nhất
    @Query(value = """
        SELECT
            p.product_id,
            p.product_name,
            p.product_price,
            p.create_date,
            GROUP_CONCAT(i.img_link SEPARATOR ', ') AS product_img
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
       SELECT p.product_id, p.product_name, p.product_price, GROUP_CONCAT(i.img_link SEPARATOR ", ") as img_link
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
}
