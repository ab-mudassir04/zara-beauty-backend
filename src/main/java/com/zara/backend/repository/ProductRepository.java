package com.zara.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.zara.backend.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {

    long countByCategoryIgnoreCase(String category);

    List<Product> findByActiveTrue();

    List<Product> findByCategoryIgnoreCase(String category);

    List<Product> findByActiveTrueAndCategoryIgnoreCase(String category);
}