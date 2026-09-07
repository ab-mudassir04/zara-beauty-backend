package com.zara.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.zara.backend.entity.Review;

public interface ReviewRepository
        extends JpaRepository<Review, Long> {

    List<Review> findByStatus(String status);

    List<Review> findByProductId(Long productId);

    List<Review> findByProductIdAndStatus(
            Long productId,
            String status
    );

    List<Review> findByCustomerId(Long customerId);
}