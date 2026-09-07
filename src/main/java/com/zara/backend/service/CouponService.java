package com.zara.backend.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.zara.backend.entity.Coupon;
import com.zara.backend.repository.CouponRepository;

@Service
public class CouponService {

    private final CouponRepository couponRepository;

    public CouponService(CouponRepository couponRepository) {
        this.couponRepository = couponRepository;
    }

    // =====================================================
    // GET ALL COUPONS
    // =====================================================

    public List<Coupon> getAllCoupons() {

        return couponRepository.findAll();
    }

    // =====================================================
    // CREATE COUPON
    // =====================================================

    public Coupon createCoupon(Coupon coupon) {

        // NULL CHECK
        if (coupon == null) {
            throw new RuntimeException(
                    "Coupon data is required"
            );
        }

        // COUPON CODE VALIDATION
        if (coupon.getCode() == null ||
                coupon.getCode().trim().isEmpty()) {

            throw new RuntimeException(
                    "Coupon code is required"
            );
        }

        // DISCOUNT VALIDATION
        if (coupon.getDiscount() == null ||
                coupon.getDiscount() <= 0 ||
                coupon.getDiscount() > 100) {

            throw new RuntimeException(
                    "Discount must be between 1 and 100"
            );
        }

        // NORMALIZE CODE
        String code = coupon.getCode()
                .trim()
                .toUpperCase();

        // DUPLICATE CODE CHECK
        if (couponRepository.existsByCodeIgnoreCase(code)) {

            throw new RuntimeException(
                    "Coupon code already exists"
            );
        }

        coupon.setCode(code);

        // MINIMUM ORDER
        if (coupon.getMinimumOrder() == null) {

            coupon.setMinimumOrder(0.0);

        } else if (coupon.getMinimumOrder() < 0) {

            throw new RuntimeException(
                    "Minimum order cannot be negative"
            );
        }

        // ACTIVE DEFAULT
        if (coupon.getActive() == null) {

            coupon.setActive(true);
        }

        // SAVE
        return couponRepository.save(coupon);
    }

    // =====================================================
    // GET COUPON BY ID
    // =====================================================

    public Coupon getCouponById(Long id) {

        if (id == null) {

            throw new RuntimeException(
                    "Coupon ID is required"
            );
        }

        return couponRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Coupon not found with id: " + id
                        )
                );
    }

    // =====================================================
    // UPDATE COUPON
    // =====================================================

    public Coupon updateCoupon(
            Long id,
            Coupon updatedCoupon) {

        if (updatedCoupon == null) {

            throw new RuntimeException(
                    "Coupon data is required"
            );
        }

        Coupon existing = getCouponById(id);

        // -------------------------------------------------
        // CODE
        // -------------------------------------------------

        if (updatedCoupon.getCode() != null &&
                !updatedCoupon.getCode()
                        .trim()
                        .isEmpty()) {

            String code = updatedCoupon.getCode()
                    .trim()
                    .toUpperCase();

            if (!code.equalsIgnoreCase(
                    existing.getCode())
                    && couponRepository
                            .existsByCodeIgnoreCase(code)) {

                throw new RuntimeException(
                        "Coupon code already exists"
                );
            }

            existing.setCode(code);
        }

        // -------------------------------------------------
        // DISCOUNT
        // -------------------------------------------------

        if (updatedCoupon.getDiscount() != null) {

            if (updatedCoupon.getDiscount() <= 0 ||
                    updatedCoupon.getDiscount() > 100) {

                throw new RuntimeException(
                        "Discount must be between 1 and 100"
                );
            }

            existing.setDiscount(
                    updatedCoupon.getDiscount()
            );
        }

        // -------------------------------------------------
        // MINIMUM ORDER
        // -------------------------------------------------

        if (updatedCoupon.getMinimumOrder() != null) {

            if (updatedCoupon.getMinimumOrder() < 0) {

                throw new RuntimeException(
                        "Minimum order cannot be negative"
                );
            }

            existing.setMinimumOrder(
                    updatedCoupon.getMinimumOrder()
            );
        }

        // -------------------------------------------------
        // EXPIRY DATE
        // -------------------------------------------------

        if (updatedCoupon.getExpiryDate() != null) {

            existing.setExpiryDate(
                    updatedCoupon.getExpiryDate()
            );
        }

        // -------------------------------------------------
        // ACTIVE
        // -------------------------------------------------

        if (updatedCoupon.getActive() != null) {

            existing.setActive(
                    updatedCoupon.getActive()
            );
        }

        // SAVE UPDATED COUPON
        return couponRepository.save(existing);
    }

    // =====================================================
    // DELETE COUPON
    // =====================================================

    public void deleteCoupon(Long id) {

        if (id == null) {

            throw new RuntimeException(
                    "Coupon ID is required"
            );
        }

        if (!couponRepository.existsById(id)) {

            throw new RuntimeException(
                    "Coupon not found with id: " + id
            );
        }

        couponRepository.deleteById(id);
    }
}