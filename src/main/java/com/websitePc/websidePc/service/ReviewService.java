package com.websitePc.websidePc.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.websitePc.websidePc.exception.ApplicationException;
import com.websitePc.websidePc.model.Review;
import com.websitePc.websidePc.model.User;
import com.websitePc.websidePc.repository.ReviewRepository;
import com.websitePc.websidePc.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ReviewService{
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;

    public ReviewService(ReviewRepository reviewRepository, UserRepository userRepository) {
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
    }

    public Page<Object[]> getReviewByProductId(Long productId, int page, int size) {
        return reviewRepository.listReviewByProductId(productId, PageRequest.of(page, size));
    }

    @Transactional
    public void addReview(JsonNode reviewData) {
        String comment = reviewData.get("comment").asText();
        int score = reviewData.get("score").asInt();
        Long productId = reviewData.get("productId").asLong();
        String email = reviewData.get("email").asText();

        // Tạo thời gian hiện tại
        LocalDateTime createDate = LocalDateTime.now();

//        từ email của người dùng thì mình sẽ lấy ra được id
//        mình dùng email để có thể lấy ra từ token
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ApplicationException("USER_NOT_FOUND", "User not found with email: " + email));

        reviewRepository.insertReview(comment,score, productId, user.getUserId(), createDate);
    }

    @Transactional
    public void updateReview(JsonNode reviewData) {
        Long reviewId = reviewData.get("reviewId").asLong();
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ApplicationException("REVIEW_NOT_FOUND", "Review not found"));

        // Cập nhật từng trường nếu có trong JSON
        if (reviewData.has("comment")) {
            review.setComment(reviewData.get("comment").asText());
        }

        if (reviewData.has("score")) {
            review.setScore(reviewData.get("score").asInt());
        }

        reviewRepository.save(review);
    }

    @Transactional
    public void deleteReviewById(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ApplicationException("REVIEW_NOT_FOUND", "Review not found"));
        reviewRepository.deleteReviewById(review.getReviewId());
    }

}
