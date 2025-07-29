package com.websitePc.websidePc.repository;

import com.websitePc.websidePc.model.ProductComponent;
import com.websitePc.websidePc.model.ProductComponentId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductComponentRepository extends JpaRepository<ProductComponent, ProductComponentId> {

//    phục vụ cho việc thêm sản phẩm
    @Modifying
    @Query(value = """
            INSERT INTO product_component (
                                    component_id,
                                    product_id
                                  )
            VALUES (:componentId, :productId)
            """, nativeQuery = true)
    void insertProductComponent(
            Long componentId,
            Long productId
    );
}
