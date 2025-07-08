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
                   WHERE u.email = :email AND p.product_id = :productId
                   LIMIT 1
               ) AS has_purchased;
        """, nativeQuery = true)
    Long hasUserBuyProduct (String email, Long productId);

// này để truy vấn sản phẩm và số lượng theo userId (giống như giỏ hàng)
//    quantity là số lượng sản phẩm mà user đã bỏ vào giỏ hàng
    @Query(value = """
          SELECT
                    p.product_id,
                    p.product_name,
                    p.product_price,
                    (SELECT i.img_link FROM img i WHERE i.product_id = p.product_id LIMIT 1) AS img_link,
                    up.quantity
                    FROM user_product up
                    JOIN product p ON up.product_id = p.product_id
                    WHERE up.user_id = :userId;
        """, nativeQuery = true)
    List<Object[]> productByUserId (String userId);

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
