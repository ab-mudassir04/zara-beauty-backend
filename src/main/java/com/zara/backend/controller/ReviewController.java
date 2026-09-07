package com.zara.backend.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zara.backend.dto.ReviewDTO;
import com.zara.backend.entity.Review;
import com.zara.backend.service.ReviewService;

@RestController
@RequestMapping("/reviews")
@CrossOrigin(origins = "http://localhost:5173")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    // ==========================================
    // GET ALL REVIEWS
    // GET /reviews
    // ==========================================

    @GetMapping
    public ResponseEntity<List<Review>> getAllReviews() {

        return ResponseEntity.ok(
                reviewService.getAllReviews()
        );
    }

    // ==========================================
    // GET REVIEW BY ID
    // GET /reviews/{id}
    // ==========================================

    @GetMapping("/{id}")
    public ResponseEntity<Review> getReviewById(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                reviewService.getReviewById(id)
        );
    }

    // ==========================================
    // GET PRODUCT REVIEWS
    // GET /reviews/product/{productId}
    // ==========================================

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<Review>> getProductReviews(
            @PathVariable Long productId
    ) {

        return ResponseEntity.ok(
                reviewService.getApprovedProductReviews(productId)
        );
    }

    // ==========================================
    // GET CUSTOMER REVIEWS
    // GET /reviews/customer/{customerId}
    // ==========================================

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<Review>> getCustomerReviews(
            @PathVariable Long customerId
    ) {

        return ResponseEntity.ok(
                reviewService.getCustomerReviews(customerId)
        );
    }

    // ==========================================
    // CREATE REVIEW
    // POST /reviews
    // ==========================================

    @PostMapping
    public ResponseEntity<Review> createReview(
            @RequestBody ReviewDTO dto
    ) {

        return ResponseEntity.ok(
                reviewService.createReview(dto)
        );
    }

    // ==========================================
    // UPDATE REVIEW STATUS
    // PUT /reviews/{id}/status
    // ==========================================

    @PutMapping("/{id}/status")
    public ResponseEntity<Review> updateStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> request
    ) {

        String status = request.get("status");

        return ResponseEntity.ok(
                reviewService.updateStatus(
                        id,
                        status
                )
        );
    }

    // ==========================================
    // DELETE REVIEW
    // DELETE /reviews/{id}
    // ==========================================

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteReview(
            @PathVariable Long id
    ) {

        reviewService.deleteReview(id);

        return ResponseEntity.ok(
                "Review deleted successfully"
        );
    }
}