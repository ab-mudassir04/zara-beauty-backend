package com.zara.backend.dto;

public class CreateOrderRequest {

    private Long userId;

    private String customerName;

    private String phone;

    private String address;

    private Double totalAmount;

    private String paymentMethod;


    // ==========================================
    // GETTERS
    // ==========================================

    public Long getUserId() {
        return userId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getPhone() {
        return phone;
    }

    public String getAddress() {
        return address;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }


    // ==========================================
    // SETTERS
    // ==========================================

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
}