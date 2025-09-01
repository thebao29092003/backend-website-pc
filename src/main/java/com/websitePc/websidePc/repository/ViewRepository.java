package com.websitePc.websidePc.repository;

import com.websitePc.websidePc.dto.AiDto.UserViewProduct;
import com.websitePc.websidePc.model.View;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ViewRepository extends JpaRepository<View, Long> {
//    show ra những product mà user đã xem qua và xem bao nhiêu lần
//    và cũng giới hạn 10 sản phẩm được xem nhiều nhất thôi
    @Query(value = """
               SELECT
                    p.product_id,
                    p.product_in_stock,
                    v.time_to_view as view_count
                FROM view v
                JOIN user u ON v.user_id = u.user_id
                JOIN product p ON v.product_id = p.product_id
                WHERE u.user_id = :userId
                AND p.product_active = 'true'
                LIMIT 5;
            """, nativeQuery = true)
    List<UserViewProduct> productByView(String userId);
}
