package com.websitePc.websidePc.repository;

import com.websitePc.websidePc.model.OrderProduct;
import com.websitePc.websidePc.model.OrderProductId;
import com.websitePc.websidePc.model.Orders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface OrderProductRepository extends JpaRepository<OrderProduct, OrderProductId> {
    @Modifying
    @Query(value = """
            INSERT INTO order_product (quantity, order_id, product_id)
            VALUES (:quantity, :orderId, :productId)
            """, nativeQuery = true)
    void insertOrderProduct(
             Integer quantity,
             String orderId,
             Long productId
    );

    @Query(value = """
            SELECT
                 p.product_id,
                 op.quantity
            FROM order_product op
            JOIN product p ON p.product_id = op.product_id
            WHERE op.order_id = :orderId;
            """, nativeQuery = true)
    List<Object[]> findByOrderId(
            String orderId
    );

}
