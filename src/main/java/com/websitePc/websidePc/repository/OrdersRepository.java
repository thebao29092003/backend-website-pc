package com.websitePc.websidePc.repository;

import com.websitePc.websidePc.model.Orders;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;

@Repository
public interface OrdersRepository extends JpaRepository<Orders, Long> {
    @Modifying
    @Query(value = """
            INSERT INTO orders (user_id, sum_price, create_date, order_id, status)
            VALUES (:userId, :price, :createDate, :orderId, :status)
            """, nativeQuery = true)
    void insertOrder(
            String userId,
            BigDecimal price,
            LocalDate createDate,
            String orderId,
            String status
    );

    // Hàm LAST_INSERT_ID() trong MySQL trả về giá trị AUTO_INCREMENT
    // đầu tiên được tạo ra bởi câu lệnh INSERT gần đây nhất trong phiên làm việc hiện tại.
    @Query(value = """
            SELECT * FROM orders WHERE order_id = :orderId
            """, nativeQuery = true)
    Orders findOrderByOrderId(String orderId);

    @Query(value = """
               SELECT
                 o.order_id,
                 o.create_date,
                 o.sum_price
               FROM
                 orders o
               WHERE o.user_id = :userId
            """,
            nativeQuery = true)
    Page<Object[]> getOrderByUserId(@Param("userId") String userId, Pageable pageable);
}
