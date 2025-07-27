package com.websitePc.websidePc.repository;

import com.websitePc.websidePc.model.Component;
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
public interface ComponentRepository extends JpaRepository<Component, Long> {

    @Modifying
    @Query(value = """
            INSERT INTO component (
                                    component_name,
                                    component_type,
                                    component_active
                                  )
            VALUES (:componentName, :componentType, "true")
            """, nativeQuery = true)
    void insertComponent(
            String componentName,
            String componentType
    );

//   ở đây thanh vì delete thì mình chuyển component_active thành false
//   để ẩn nó đi khỏi admin (chỗ thêm linh kiện cho pc) nhưng nó vẫn còn trong database
    @Modifying
    @Query(value = """
        UPDATE component c
        SET c.component_active = "false"
        WHERE c.component_id = :componentId
    """, nativeQuery = true)
    void deleteComponentById(Long componentId);

    @Query(value = """
        SELECT
            c.component_id,
            c.component_name,
            c.component_type
        FROM
            component c
        WHERE
            c.component_active = "true" AND
            c.component_name LIKE CONCAT('%', :name, '%')
        ORDER BY
            c.component_type ASC
        """,
            nativeQuery = true)
    Page<Object[]> listComponentByName(
            Pageable pageable,
            String name
    );

    @Query(value = """
        SELECT
            c.component_id,
            c.component_name,
            c.component_type
        FROM
            component c
        WHERE  c.component_active = "true"
        ORDER BY
            c.component_type ASC
        """,
            nativeQuery = true)
    Page<Object[]> listComponentForAdmin(
            Pageable pageable
    );

    //    phục vụ cho api chi tiết sản phẩm
    @Query(value = """
        SELECT c.component_name, c.component_type
        FROM product p
        left join product_component pc
        on p.product_id = pc.product_id
        join component c on c.component_id = pc.component_id
        where p.product_id = :productId
        """, nativeQuery = true)
    List<Object> findComponentByProductId(Long productId);
}
