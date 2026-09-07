package com.zara.backend.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zara.backend.dto.CreateOrderRequest;
import com.zara.backend.entity.Order;
import com.zara.backend.entity.OrderStatus;
import com.zara.backend.entity.Payment;
import com.zara.backend.entity.PaymentMethod;
import com.zara.backend.entity.PaymentStatus;
import com.zara.backend.entity.User;
import com.zara.backend.repository.OrderRepository;
import com.zara.backend.repository.PaymentRepository;
import com.zara.backend.repository.UserRepository;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final PaymentRepository paymentRepository;

    public OrderService(
            OrderRepository orderRepository,
            UserRepository userRepository,
            PaymentRepository paymentRepository) {

        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.paymentRepository = paymentRepository;
    }

    // =====================================================
    // CREATE ORDER
    // =====================================================

    @Transactional
    public Order createOrder(
            CreateOrderRequest request,
            String authenticatedEmail) {

        if (request == null) {
            throw new RuntimeException("Order data is required");
        }

        if (authenticatedEmail == null ||
                authenticatedEmail.trim().isEmpty()) {

            throw new SecurityException(
                    "Authenticated user is required");
        }

        if (request.getUserId() == null) {
            throw new RuntimeException("User ID is required");
        }

        if (request.getCustomerName() == null ||
                request.getCustomerName().trim().isEmpty()) {

            throw new RuntimeException("Customer name is required");
        }

        if (request.getPhone() == null ||
                request.getPhone().trim().isEmpty()) {

            throw new RuntimeException("Phone is required");
        }

        if (request.getAddress() == null ||
                request.getAddress().trim().isEmpty()) {

            throw new RuntimeException("Address is required");
        }

        if (request.getTotalAmount() == null ||
                request.getTotalAmount() <= 0) {

            throw new RuntimeException("Invalid order amount");
        }

        if (request.getPaymentMethod() == null ||
                request.getPaymentMethod().trim().isEmpty()) {

            throw new RuntimeException(
                    "Payment method is required");
        }

        // =================================================
        // FIND AUTHENTICATED USER
        // =================================================

        String email = authenticatedEmail
                .trim()
                .toLowerCase();

        User authenticatedUser =
                userRepository
                        .findByEmail(email)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Authenticated user not found"));

        // =================================================
        // OWNERSHIP SECURITY
        // =================================================

        if (!authenticatedUser.getId()
                .equals(request.getUserId())) {

            throw new SecurityException(
                    "You can only create an order for your own account");
        }

        // =================================================
        // PAYMENT METHOD
        // =================================================

        PaymentMethod paymentMethod;

        try {

            paymentMethod =
                    PaymentMethod.valueOf(
                            request.getPaymentMethod()
                                    .trim()
                                    .toUpperCase());

        } catch (IllegalArgumentException e) {

            throw new RuntimeException(
                    "Invalid payment method. Use COD or RAZORPAY.");
        }

        // =================================================
        // CREATE ORDER
        // =================================================

        Order order = new Order();

        order.setUser(authenticatedUser);

        order.setCustomerName(
                request.getCustomerName().trim());

        order.setPhone(
                request.getPhone().trim());

        order.setAddress(
                request.getAddress().trim());

        order.setTotalAmount(
                request.getTotalAmount());

        order.setPaymentMethod(
                paymentMethod);

        order.setPaymentStatus(
                PaymentStatus.PENDING);

        order.setOrderStatus(
                OrderStatus.PENDING);

        // =================================================
        // COD
        // =================================================

        if (paymentMethod == PaymentMethod.COD) {

            order.setOrderStatus(
                    OrderStatus.CONFIRMED);
        }

        // =================================================
        // SAVE ORDER
        // =================================================

        Order savedOrder =
                orderRepository.save(order);

        // =================================================
        // CREATE PAYMENT
        // =================================================

        Payment payment = new Payment();

        payment.setOrderId(
                savedOrder.getId());

        payment.setAmount(
                savedOrder.getTotalAmount());

        payment.setPaymentMethod(
                paymentMethod);

        payment.setPaymentStatus(
                PaymentStatus.PENDING);

        payment.setRazorpayOrderId(null);
        payment.setRazorpayPaymentId(null);

        paymentRepository.save(payment);

        return savedOrder;
    }

    // =====================================================
    // GET ORDER BY ID
    // =====================================================

    @Transactional(readOnly = true)
    public Order getOrderById(Long orderId) {

        if (orderId == null) {
            throw new RuntimeException(
                    "Order ID is required");
        }

        return orderRepository
                .findOrderWithUser(orderId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Order not found"));
    }

    // =====================================================
    // GET ALL ORDERS
    // =====================================================

    @Transactional(readOnly = true)
    public List<Order> getAllOrders() {

        return orderRepository
                .findAllOrdersWithUser();
    }

    // =====================================================
    // GET USER ORDERS
    // =====================================================

    @Transactional(readOnly = true)
    public List<Order> getUserOrders(
            Long userId,
            String authenticatedEmail) {

        if (userId == null) {
            throw new RuntimeException(
                    "User ID is required");
        }

        if (authenticatedEmail == null ||
                authenticatedEmail.trim().isEmpty()) {

            throw new SecurityException(
                    "User is not authenticated");
        }

        String email = authenticatedEmail
                .trim()
                .toLowerCase();

        User authenticatedUser =
                userRepository
                        .findByEmail(email)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Authenticated user not found"));

        // =================================================
        // OWNERSHIP SECURITY
        // =================================================

        if (!authenticatedUser.getId()
                .equals(userId)) {

            throw new SecurityException(
                    "You are not authorized to view these orders");
        }

        return orderRepository
                .findByUserOrderByCreatedAtDesc(
                        authenticatedUser);
    }

    // =====================================================
    // UPDATE ORDER STATUS
    // =====================================================

    @Transactional
    public Order updateOrderStatus(
            Long orderId,
            OrderStatus status) {

        if (orderId == null) {
            throw new RuntimeException(
                    "Order ID is required");
        }

        if (status == null) {
            throw new RuntimeException(
                    "Order status is required");
        }

        /*
         * IMPORTANT:
         * Fetch User together with Order.
         *
         * This allows OrderResponse.fromEntity()
         * to safely access user information.
         */
        Order order =
                orderRepository
                        .findOrderWithUser(orderId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Order not found"));

        // =================================================
        // UPDATE STATUS
        // =================================================

        order.setOrderStatus(status);

        // =================================================
        // DELIVERED
        // =================================================

        if (status == OrderStatus.DELIVERED) {

            order.setPaymentStatus(
                    PaymentStatus.PAID);

            Payment payment =
                    paymentRepository
                            .findByOrderId(order.getId())
                            .orElse(null);

            // =============================================
            // CREATE PAYMENT IF MISSING
            // =============================================

            if (payment == null) {

                payment = new Payment();

                payment.setOrderId(
                        order.getId());

                payment.setAmount(
                        order.getTotalAmount());

                payment.setPaymentMethod(
                        order.getPaymentMethod());
            }

            // =============================================
            // PRESERVE PAYMENT METHOD
            // =============================================

            if (payment.getPaymentMethod() == null) {

                payment.setPaymentMethod(
                        order.getPaymentMethod());
            }

            payment.setAmount(
                    order.getTotalAmount());

            payment.setPaymentStatus(
                    PaymentStatus.PAID);

            paymentRepository.save(payment);
        }

        return orderRepository.save(order);
    }

    // =====================================================
    // UPDATE PAYMENT STATUS
    // =====================================================

    @Transactional
    public Order updatePaymentStatus(
            Long orderId,
            PaymentStatus paymentStatus) {

        if (orderId == null) {
            throw new RuntimeException(
                    "Order ID is required");
        }

        if (paymentStatus == null) {
            throw new RuntimeException(
                    "Payment status is required");
        }

        /*
         * Fetch User together with Order.
         */
        Order order =
                orderRepository
                        .findOrderWithUser(orderId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Order not found"));

        // =================================================
        // UPDATE ORDER PAYMENT STATUS
        // =================================================

        order.setPaymentStatus(
                paymentStatus);

        // =================================================
        // FIND PAYMENT
        // =================================================

        Payment payment =
                paymentRepository
                        .findByOrderId(
                                order.getId())
                        .orElse(null);

        // =================================================
        // CREATE PAYMENT IF MISSING
        // =================================================

        if (payment == null) {

            payment = new Payment();

            payment.setOrderId(
                    order.getId());

            payment.setAmount(
                    order.getTotalAmount());

            payment.setPaymentMethod(
                    order.getPaymentMethod());
        }

        // =================================================
        // UPDATE PAYMENT
        // =================================================

        payment.setAmount(
                order.getTotalAmount());

        if (payment.getPaymentMethod() == null) {

            payment.setPaymentMethod(
                    order.getPaymentMethod());
        }

        payment.setPaymentStatus(
                paymentStatus);

        paymentRepository.save(payment);

        // =================================================
        // PAID + PENDING = CONFIRMED
        // =================================================

        if (paymentStatus == PaymentStatus.PAID &&
                order.getOrderStatus() == OrderStatus.PENDING) {

            order.setOrderStatus(
                    OrderStatus.CONFIRMED);
        }

        return orderRepository.save(order);
    }
}