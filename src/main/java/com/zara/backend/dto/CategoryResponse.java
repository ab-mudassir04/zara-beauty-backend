package com.zara.backend.dto;

public class CategoryResponse {

    private Long id;
    private String name;
    private Boolean active;
    private long productCount;

    public CategoryResponse() {
    }

    public CategoryResponse(
            Long id,
            String name,
            Boolean active,
            long productCount
    ) {
        this.id = id;
        this.name = name;
        this.active = active;
        this.productCount = productCount;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public long getProductCount() {
        return productCount;
    }

    public void setProductCount(long productCount) {
        this.productCount = productCount;
    }
}