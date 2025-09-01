package com.websitePc.websidePc.repository;

import com.websitePc.websidePc.dto.AiDto.ProductInput;
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
import java.util.List;

@Repository
public interface OrdersRepository extends JpaRepository<Orders, Long> {
    //    trả về số đơn hàng theo tháng trong 3,6,12 tháng gần nhất
    @Query(value = """
            SELECT
                DATE_FORMAT(create_date, '%Y-%m') AS month,
                COUNT(order_id) AS quantity_order
            FROM
                orders
            WHERE
                create_date >= DATE_SUB(CURDATE(), INTERVAL :month MONTH)
            GROUP BY
                DATE_FORMAT(create_date, '%Y-%m')
            ORDER BY
                month DESC
            limit :month;
        """,
            nativeQuery = true)
    List<Object[]> quantityOrderMonths(int month);

    //    trả về doanh thu theo tháng trong 3,6,12 tháng gần nhất
    @Query(value = """
            SELECT
                DATE_FORMAT(create_date, '%Y-%m') AS month,
                COALESCE(SUM(sum_price), 0) AS total_revenue
            FROM
                orders
            WHERE
                create_date >= DATE_SUB(CURDATE(), INTERVAL :month MONTH)
            GROUP BY
                DATE_FORMAT(create_date, '%Y-%m')
            ORDER BY
                month DESC
            limit :month;
        """,
            nativeQuery = true)
    List<Object[]> revenueMonths(int month);

    @Query(value = """
            SELECT
                o.order_id,
                o.create_date,
                o.sum_price,
                o.status,
                u.email
            FROM
                orders o
            JOIN
                user u ON o.user_id = u.user_id
        """,
            nativeQuery = true)
    Page<Object[]> listOrder(Pageable pageable);

//    dùng cho user nên chỉ show những order có status thành công
    @Query(value = """
                           SELECT
                               o.order_id,
                               o.create_date,
                               o.sum_price,
                               o.status
                           FROM
                               orders o
                           WHERE o.user_id = :userId AND o.status = "COMPLETED"
        """,
            nativeQuery = true)
    Page<Object[]> getOrderByUserId(String userId, Pageable pageable);

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


//    số sản phẩm user đã mua giới hạn tầm 10 sản phẩm mua gần nhất thôi
//    nếu nhiều quá sợ quá tải
    @Query(value = """
            SELECT
				p.product_id,
				p.product_type,
				p.product_in_stock
            FROM orders o
            JOIN order_product op ON o.order_id =  op.order_id
            JOIN product p ON p.product_id = op.product_id
            WHERE o.user_id = :userId
			AND p.product_active = 'true'
            LIMIT 5;
                """, nativeQuery = true)
    List<ProductInput> productByUser(String userId);
}
