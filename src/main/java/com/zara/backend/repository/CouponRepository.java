package com.zara.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.zara.backend.entity.Coupon;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, Long> {

    boolean existsByCodeIgnoreCase(String code);

}