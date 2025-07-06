package com.websitePc.websidePc.repository;


import com.websitePc.websidePc.model.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;


@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    @Query(value = """
            SELECT r.review_id, r.comment, r.create_date, r.score, r.product_id, u.full_name, u.user_id
            FROM review r
            JOIN user u ON r.user_id = u.user_id
            WHERE r.product_id = :productId
            ORDER BY r.create_date DESC;
            """, nativeQuery = true)
    Page<Object[]> listReviewByProductId(Long productId, Pageable pageable);

    @Modifying
    @Query(value = """
        INSERT INTO review (comment, score, product_id, user_id, create_date)
        VALUES (:comment, :score, :productId, :userId, :createDate)
        """, nativeQuery = true)
    void insertReview(
            String comment,
            int score,
            Long productId,
            String userId,
            LocalDateTime createDate
    );

    @Modifying
    @Query(value = """
            DELETE FROM review
            WHERE review_id = :reviewId
        """, nativeQuery = true)
    void deleteReviewById(Long reviewId);

}