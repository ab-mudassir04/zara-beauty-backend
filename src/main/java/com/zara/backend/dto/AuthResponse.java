package com.zara.backend.dto;

public class AuthResponse {

    private Long id;
    private String token;
    private String role;
    private String name;
    private String email;
    private String phone;
    private String address;

    public AuthResponse() {
    }

    public AuthResponse(
            Long id,
            String token,
            String role,
            String name,
            String email,
            String phone,
            String address
    ) {
        this.id = id;
        this.token = token;
        this.role = role;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.address = address;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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
}