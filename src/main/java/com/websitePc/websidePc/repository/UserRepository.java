package com.websitePc.websidePc.repository;

import com.websitePc.websidePc.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Boolean existsByEmail(String email);
    Boolean existsByPhone(String email);
    //  Câu truy vấn này kiểm tra xem một người dùng cụ thể đã mua một
    //  product cụ thể hay chưa.
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
}
