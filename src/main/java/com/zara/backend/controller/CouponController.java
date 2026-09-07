package com.zara.backend.controller;

import java.util.List;

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

import com.zara.backend.entity.Coupon;
import com.zara.backend.service.CouponService;

@RestController
@RequestMapping("/coupons")
@CrossOrigin(origins = "http://localhost:5173")
public class CouponController {

    private final CouponService couponService;

    public CouponController(CouponService couponService) {
        this.couponService = couponService;
    }

    // =====================================================
    // GET ALL
    // =====================================================

    @GetMapping
    public ResponseEntity<List<Coupon>> getAllCoupons() {

        return ResponseEntity.ok(
                couponService.getAllCoupons()
        );
    }

    // =====================================================
    // GET BY ID
    // =====================================================

    @GetMapping("/{id}")
    public ResponseEntity<Coupon> getCoupon(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                couponService.getCouponById(id)
        );
    }

    // =====================================================
    // CREATE
    // =====================================================

    @PostMapping
    public ResponseEntity<Coupon> createCoupon(
            @RequestBody Coupon coupon
    ) {

        return ResponseEntity.ok(
                couponService.createCoupon(coupon)
        );
    }

    // =====================================================
    // UPDATE
    // =====================================================

    @PutMapping("/{id}")
    public ResponseEntity<Coupon> updateCoupon(
            @PathVariable Long id,
            @RequestBody Coupon coupon
    ) {

        return ResponseEntity.ok(
                couponService.updateCoupon(id, coupon)
        );
    }

    // =====================================================
    // DELETE
    // =====================================================

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCoupon(
            @PathVariable Long id
    ) {

        couponService.deleteCoupon(id);

        return ResponseEntity.ok(
                "Coupon deleted successfully"
        );
    }
}