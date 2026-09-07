package com.zara.backend.dto;

import java.time.LocalDateTime;

import com.zara.backend.entity.Order;
import com.zara.backend.entity.OrderStatus;
import com.zara.backend.entity.PaymentMethod;
import com.zara.backend.entity.PaymentStatus;

public class OrderResponse {

    private Long id;

    private Long userId;
    private String userName;
    private String userEmail;

    private String customerName;
    private String phone;
    private String address;

    private Double totalAmount;

    private PaymentMethod paymentMethod;
    private PaymentStatus paymentStatus;
    private OrderStatus orderStatus;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public OrderResponse() {
    }

    // =====================================================
    // ENTITY -> DTO
    // =====================================================

    public static OrderResponse fromEntity(Order order) {

        if (order == null) {
            return null;
        }

        OrderResponse response = new OrderResponse();

        response.setId(order.getId());

        /*
         * User is explicitly fetched using @EntityGraph
         * in OrderRepository.
         */
        if (order.getUser() != null) {

            response.setUserId(
                    order.getUser().getId()
            );

            response.setUserName(
                    order.getUser().getName()
            );

            response.setUserEmail(
                    order.getUser().getEmail()
            );
        }

        response.setCustomerName(
                order.getCustomerName()
        );

        response.setPhone(
                order.getPhone()
        );

        response.setAddress(
                order.getAddress()
        );

        response.setTotalAmount(
                order.getTotalAmount()
        );

        response.setPaymentMethod(
                order.getPaymentMethod()
        );

        response.setPaymentStatus(
                order.getPaymentStatus()
        );

        response.setOrderStatus(
                order.getOrderStatus()
        );

        response.setCreatedAt(
                order.getCreatedAt()
        );

        response.setUpdatedAt(
                order.getUpdatedAt()
        );

        return response;
    }

    // =====================================================
    // GETTERS / SETTERS
    // =====================================================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}