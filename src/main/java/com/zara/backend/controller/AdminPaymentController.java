package com.zara.backend.controller;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zara.backend.entity.Order;
import com.zara.backend.entity.OrderStatus;
import com.zara.backend.entity.Payment;
import com.zara.backend.entity.PaymentStatus;
import com.zara.backend.repository.OrderRepository;
import com.zara.backend.repository.PaymentRepository;
import com.zara.backend.service.RazorpayService;

@RestController
@RequestMapping("/api/admin/payments")
@CrossOrigin(origins = "http://localhost:5173")
public class AdminPaymentController {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final RazorpayService razorpayService;

    public AdminPaymentController(
            PaymentRepository paymentRepository,
            OrderRepository orderRepository,
            RazorpayService razorpayService
    ) {
        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
        this.razorpayService = razorpayService;
    }

    // =====================================================
    // GET ALL PAYMENTS
    // =====================================================

    @GetMapping
    public ResponseEntity<?> getAllPayments() {

        try {

            List<Payment> payments =
                    paymentRepository.findAll();

            payments.sort(
                    Comparator.comparing(
                            Payment::getCreatedAt,
                            Comparator.nullsLast(
                                    Comparator.reverseOrder()
                            )
                    )
            );

            return ResponseEntity.ok(payments);

        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }

    // =====================================================
    // GET PAYMENT BY ID
    // =====================================================

    @GetMapping("/{id}")
    public ResponseEntity<?> getPaymentById(
            @PathVariable Long id
    ) {

        try {

            Payment payment =
                    paymentRepository
                            .findById(id)
                            .orElseThrow(() ->
                                    new RuntimeException(
                                            "Payment not found"
                                    )
                            );

            return ResponseEntity.ok(payment);

        } catch (Exception e) {

            return ResponseEntity
                    .notFound()
                    .build();
        }
    }

    // =====================================================
    // GET PAYMENT BY ORDER ID
    // =====================================================

    @GetMapping("/order/{orderId}")
    public ResponseEntity<?> getPaymentByOrderId(
            @PathVariable Long orderId
    ) {

        try {

            Payment payment =
                    paymentRepository
                            .findByOrderId(orderId)
                            .orElseThrow(() ->
                                    new RuntimeException(
                                            "Payment not found"
                                    )
                            );

            return ResponseEntity.ok(payment);

        } catch (Exception e) {

            return ResponseEntity
                    .notFound()
                    .build();
        }
    }

    // =====================================================
    // UPDATE PAYMENT STATUS
    // =====================================================

    @PutMapping("/{id}/status")
    public ResponseEntity<?> updatePaymentStatus(
            @PathVariable Long id,
            @RequestBody PaymentStatusRequest request
    ) {

        try {

            Payment payment =
                    paymentRepository
                            .findById(id)
                            .orElseThrow(() ->
                                    new RuntimeException(
                                            "Payment not found"
                                    )
                            );

            if (request == null ||
                    request.getPaymentStatus() == null) {

                throw new RuntimeException(
                        "Payment status is required"
                );
            }

            PaymentStatus paymentStatus;

            try {

                paymentStatus =
                        PaymentStatus.valueOf(
                                request.getPaymentStatus()
                                        .trim()
                                        .toUpperCase()
                        );

            } catch (Exception e) {

                throw new RuntimeException(
                        "Invalid payment status"
                );
            }

            payment.setPaymentStatus(
                    paymentStatus
            );

            paymentRepository.save(payment);

            // =================================================
            // SYNC ORDER
            // =================================================

            orderRepository
                    .findById(payment.getOrderId())
                    .ifPresent(order -> {

                        order.setPaymentStatus(
                                paymentStatus
                        );

                        if (paymentStatus ==
                                PaymentStatus.PAID &&
                                order.getOrderStatus() ==
                                        OrderStatus.PENDING) {

                            order.setOrderStatus(
                                    OrderStatus.CONFIRMED
                            );
                        }

                        orderRepository.save(order);
                    });

            return ResponseEntity.ok(payment);

        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }

    // =====================================================
    // REFUND RAZORPAY PAYMENT
    // =====================================================

    @PostMapping("/{id}/refund")
    public ResponseEntity<?> refundPayment(
            @PathVariable Long id
    ) {

        try {

            Payment payment =
                    paymentRepository
                            .findById(id)
                            .orElseThrow(() ->
                                    new RuntimeException(
                                            "Payment not found"
                                    )
                            );

            // =================================================
            // CHECK METHOD
            // =================================================

            if (payment.getPaymentMethod() == null ||
                    !"RAZORPAY".equals(
                            payment.getPaymentMethod().name()
                    )) {

                throw new RuntimeException(
                        "Only Razorpay payments can be refunded."
                );
            }

            // =================================================
            // CHECK STATUS
            // =================================================

            if (payment.getPaymentStatus()
                    != PaymentStatus.PAID) {

                throw new RuntimeException(
                        "Only paid payments can be refunded."
                );
            }

            // =================================================
            // CHECK RAZORPAY PAYMENT ID
            // =================================================

            if (payment.getRazorpayPaymentId() == null ||
                    payment.getRazorpayPaymentId()
                            .trim()
                            .isEmpty()) {

                throw new RuntimeException(
                        "Razorpay payment ID not found."
                );
            }

            // =================================================
            // RAZORPAY REFUND
            // =================================================

            razorpayService.refundPayment(
                    payment.getRazorpayPaymentId(),
                    payment.getAmount()
            );

            // =================================================
            // UPDATE PAYMENT
            // =================================================

            payment.setPaymentStatus(
                    PaymentStatus.REFUNDED
            );

            paymentRepository.save(payment);

            // =================================================
            // UPDATE ORDER
            // =================================================

            orderRepository
                    .findById(payment.getOrderId())
                    .ifPresent(order -> {

                        order.setPaymentStatus(
                                PaymentStatus.REFUNDED
                        );

                        order.setOrderStatus(
                                OrderStatus.CANCELLED
                        );

                        orderRepository.save(order);
                    });

            return ResponseEntity.ok(
                    Map.of(
                            "message",
                            "Payment refunded successfully",

                            "paymentId",
                            payment.getId(),

                            "orderId",
                            payment.getOrderId(),

                            "amount",
                            payment.getAmount(),

                            "paymentStatus",
                            payment.getPaymentStatus()
                    )
            );

        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }

    // =====================================================
    // REQUEST CLASS
    // =====================================================

    public static class PaymentStatusRequest {

        private String paymentStatus;

        public String getPaymentStatus() {
            return paymentStatus;
        }

        public void setPaymentStatus(
                String paymentStatus
        ) {
            this.paymentStatus = paymentStatus;
        }
    }
}