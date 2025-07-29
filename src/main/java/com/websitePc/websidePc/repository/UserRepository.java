package com.websitePc.websidePc.repository;

import com.websitePc.websidePc.model.User;
import com.websitePc.websidePc.model.UserProduct;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    Boolean existsByEmail(String email);

    Boolean existsByPhone(String phone);

    @Modifying
    @Query(value = """
        UPDATE user u
        SET u.role = :role
        WHERE u.user_id = :userId
        """,
            nativeQuery = true)
    void toggleAdmin(String userId, String role);

    //    AND o.status = "COMPLETED": là order mà user đã thanh toán thành công
    //    trả về thêm role để có thể thay đổi quyền hạn của user
    @Query(value = """
                                SELECT
                                    u.user_id,
                                    u.full_name,
                                    u.email,
                                    u.phone,
                                    COALESCE(SUM(o.sum_price), 0) AS total_spent,
                                    u.role
                                FROM
                                    user u
                                LEFT JOIN
                                    orders o ON u.user_id = o.user_id
                                WHERE u.user_id = :userId AND o.status = "COMPLETED"
                                GROUP BY
                                    u.user_id
            """,
            nativeQuery = true)
    Object getUserTotalSpent(String userId);

    //    nó luôn cố định 12 thàng nên không cần phân trang
    //    AND o.status = "COMPLETED": là order mà user đã thanh toán thành công
    @Query(value = """
                SELECT
                    DATE_FORMAT(create_date, '%Y-%m') AS month,
                    SUM(sum_price) AS total_spent
                FROM
                    orders o
                WHERE
                    o.user_id = :userId
                    AND create_date >= DATE_SUB(CURRENT_DATE(), INTERVAL :month MONTH)
                    AND o.status = "COMPLETED"
                GROUP BY
                    DATE_FORMAT(create_date, '%Y-%m')
                ORDER BY
                    month DESC
                limit :month;
            """,
            nativeQuery = true)
    List<Object[]> getSpentPerMonth(String userId, int month);

//    COALESCE để thay thế NULL thành 0 nếu user không có đơn hàng nào
    @Query(value = """
                SELECT
                    u.user_id,
                    u.full_name,
                    u.email,
                    COUNT(o.order_id) AS total_orders,
                    COALESCE(SUM(o.sum_price), 0) AS total_spent
                FROM
                    user u
                LEFT JOIN
                     orders o ON u.user_id = o.user_id
                WHERE
                    u.full_name LIKE CONCAT('%', :userName, '%')
                GROUP BY
                    u.user_id
                order by total_orders desc
            """,
            nativeQuery = true)
    Page<Object[]> findUserByName(@Param("userName") String userName, Pageable pageable);

    @Query(value = """
                SELECT
                    u.user_id,
                    u.full_name,
                    u.email,
                    COUNT(o.order_id) AS total_orders,
                    COALESCE(SUM(o.sum_price), 0) AS total_spent
                FROM
                    user u
                LEFT JOIN
                    orders o ON u.user_id = o.user_id
                GROUP BY
                    u.user_id
                order by total_orders desc
            """,
            nativeQuery = true)
    Page<Object[]> listUser(Pageable pageable);

    @Modifying
    @Query(value = """
                UPDATE user u
                SET u.full_name = :fullName, u.phone = :phone
                WHERE u.user_id = :userId
            """, nativeQuery = true)
    void updateUser(String userId, String fullName, String phone);


    //  Câu truy vấn này kiểm tra xem một người dùng cụ thể đã mua một
    //  product cụ thể hay chưa. dùng email hoặc userId đều đc cái nào tiện thì dùng
    //  xem note
    @Query(value = """
               SELECT EXISTS (
                       SELECT 1
                       FROM user u
                       JOIN orders o ON o.user_id = u.user_id
                       JOIN order_product op ON op.order_id = o.order_id
                       JOIN product p ON p.product_id = op.product_id
                       WHERE u.user_id = :userId AND p.product_id = :productId
                       LIMIT 1
                   ) AS has_purchased;
            """, nativeQuery = true)
    Long hasUserBuyProduct(String userId, Long productId);

    // này để truy vấn sản phẩm và số lượng theo userId (giống như giỏ hàng)
//    up.quantity là số lượng sản phẩm mà user đã bỏ vào giỏ hàng
//    còn p.product_in_stock là số lượng hàng tồn kho
    @Query(value = """
              SELECT
                        p.product_id,
                        p.product_name,
                        p.product_price,
                        p.product_in_stock,
                        (SELECT i.img_link FROM img i WHERE i.product_id = p.product_id LIMIT 1) AS img_link,
                        up.quantity
                        FROM user_product up
                        JOIN product p ON up.product_id = p.product_id
                        WHERE up.user_id = :userId;
            """, nativeQuery = true)
    List<Object[]> productByUserId(String userId);

    // phục vụ cho việc add cart item nếu user add cart item mà item đó đã tồn tại
//    thì sẽ tăng số lượng lên
    @Query(value = """
            SELECT *
            FROM user_product up
            WHERE up.product_id = :productId
            AND up.user_id = :userId
            """, nativeQuery = true)
    UserProduct existUserProduct(
            @Param("productId") Long productId,
            @Param("userId") String userId
    );

    @Modifying
    @Query(value = """
            INSERT INTO user_product (product_id, user_id, quantity)
            VALUES (:productId, :userId, :quantity)
            """, nativeQuery = true)
    void insertCartItem(
            @Param("productId") Long productId,
            @Param("userId") String userId,
            @Param("quantity") Integer quantity
    );

    @Modifying
    @Query(value = """
                DELETE FROM user_product
                WHERE product_id = :productId AND user_id = :userId
            """, nativeQuery = true)
    void deleteCartItem(
            Long productId,
            String userId
    );
}
