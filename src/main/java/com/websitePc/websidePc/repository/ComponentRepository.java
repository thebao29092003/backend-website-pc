package com.websitePc.websidePc.repository;

import com.websitePc.websidePc.model.Component;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComponentRepository extends JpaRepository<Component, Long> {
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
