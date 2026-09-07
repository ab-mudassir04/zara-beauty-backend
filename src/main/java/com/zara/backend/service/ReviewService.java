package com.zara.backend.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.zara.backend.dto.ReviewDTO;
import com.zara.backend.entity.Review;
import com.zara.backend.repository.ReviewRepository;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;

    public ReviewService(
            ReviewRepository reviewRepository
    ) {
        this.reviewRepository = reviewRepository;
    }

    // ==========================================
    // GET ALL REVIEWS
    // ADMIN
    // ==========================================

    public List<Review> getAllReviews() {

        return reviewRepository.findAll();
    }

    // ==========================================
    // GET REVIEW BY ID
    // ==========================================

    public Review getReviewById(Long id) {

        return reviewRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Review not found with id: " + id
                        )
                );
    }

    // ==========================================
    // GET APPROVED PRODUCT REVIEWS
    // USER SIDE
    // ==========================================

    public List<Review> getApprovedProductReviews(
            Long productId
    ) {

        return reviewRepository
                .findByProductIdAndStatus(
                        productId,
                        "APPROVED"
                );
    }

    // ==========================================
    // GET CUSTOMER REVIEWS
    // ==========================================

    public List<Review> getCustomerReviews(
            Long customerId
    ) {

        return reviewRepository
                .findByCustomerId(customerId);
    }

    // ==========================================
    // CREATE REVIEW
    // ==========================================

    public Review createReview(ReviewDTO dto) {

        if (dto == null) {

            throw new RuntimeException(
                    "Review data is required"
            );
        }

        // ======================================
        // PRODUCT VALIDATION
        // ======================================

        if (dto.getProductId() == null) {

            throw new RuntimeException(
                    "Product ID is required"
            );
        }

        // ======================================
        // CUSTOMER VALIDATION
        // ======================================

        if (dto.getCustomerId() == null) {

            throw new RuntimeException(
                    "Please login to submit a review."
            );
        }

        if (dto.getCustomerName() == null ||
                dto.getCustomerName().trim().isEmpty()) {

            throw new RuntimeException(
                    "Customer name is required"
            );
        }

        // ======================================
        // RATING VALIDATION
        // ======================================

        if (dto.getRating() == null) {

            throw new RuntimeException(
                    "Rating is required"
            );
        }

        if (dto.getRating() < 1 ||
                dto.getRating() > 5) {

            throw new RuntimeException(
                    "Rating must be between 1 and 5"
            );
        }

        // ======================================
        // COMMENT VALIDATION
        // ======================================

        if (dto.getComment() == null ||
                dto.getComment().trim().isEmpty()) {

            throw new RuntimeException(
                    "Review comment is required"
            );
        }

        // ======================================
        // CREATE REVIEW
        // ======================================

        Review review = new Review();

        review.setProductId(
                dto.getProductId()
        );

        review.setProductName(
                dto.getProductName()
        );

        review.setCustomerId(
                dto.getCustomerId()
        );

        review.setCustomerName(
                dto.getCustomerName().trim()
        );

        review.setRating(
                dto.getRating()
        );

        review.setComment(
                dto.getComment().trim()
        );

        // ======================================
        // NEW REVIEW = PENDING
        // ======================================

        review.setStatus("PENDING");

        return reviewRepository.save(review);
    }

    // ==========================================
    // UPDATE REVIEW STATUS
    // ==========================================

    public Review updateStatus(
            Long id,
            String status
    ) {

        Review review = getReviewById(id);

        if (status == null ||
                status.trim().isEmpty()) {

            throw new RuntimeException(
                    "Review status is required"
            );
        }

        String newStatus = status
                .trim()
                .toUpperCase();

        validateStatus(newStatus);

        review.setStatus(newStatus);

        return reviewRepository.save(review);
    }

    // ==========================================
    // DELETE REVIEW
    // ==========================================

    public void deleteReview(Long id) {

        if (!reviewRepository.existsById(id)) {

            throw new RuntimeException(
                    "Review not found with id: " + id
            );
        }

        reviewRepository.deleteById(id);
    }

    // ==========================================
    // VALIDATE STATUS
    // ==========================================

    private void validateStatus(String status) {

        if (!status.equals("PENDING") &&
                !status.equals("APPROVED") &&
                !status.equals("REJECTED")) {

            throw new RuntimeException(
                    "Invalid review status"
            );
        }
    }
}