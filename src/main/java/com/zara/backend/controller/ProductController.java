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

import com.zara.backend.entity.Product;
import com.zara.backend.service.ProductService;

@RestController
@RequestMapping("/products")
@CrossOrigin(origins = {
        "http://localhost:5173",
        "http://localhost:5174"
})
public class ProductController {

    private final ProductService service;

    public ProductController(ProductService service) {
        this.service = service;
    }

    // ==========================================
    // GET ALL PRODUCTS
    // ==========================================

    @GetMapping
    public ResponseEntity<List<Product>> getProducts() {

        return ResponseEntity.ok(
                service.getAllProducts()
        );
    }

    // ==========================================
    // GET ACTIVE PRODUCTS
    // ==========================================

    @GetMapping("/active")
    public ResponseEntity<List<Product>> getActiveProducts() {

        return ResponseEntity.ok(
                service.getActiveProducts()
        );
    }

    // ==========================================
    // GET PRODUCT BY ID
    // ==========================================

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                service.getProductById(id)
        );
    }

    // ==========================================
    // CREATE PRODUCT
    // ==========================================

    @PostMapping
    public ResponseEntity<Product> addProduct(
            @RequestBody Product product
    ) {

        return ResponseEntity.ok(
                service.saveProduct(product)
        );
    }

    // ==========================================
    // UPDATE PRODUCT
    // ==========================================

    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(
            @PathVariable Long id,
            @RequestBody Product product
    ) {

        return ResponseEntity.ok(
                service.updateProduct(id, product)
        );
    }

    // ==========================================
    // DELETE PRODUCT
    // ==========================================

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProduct(
            @PathVariable Long id
    ) {

        service.deleteProduct(id);

        return ResponseEntity.ok(
                "Product deleted successfully"
        );
    }

    // ==========================================
    // DEACTIVATE
    // ==========================================

    @PutMapping("/{id}/deactivate")
    public ResponseEntity<Product> deactivateProduct(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                service.deactivateProduct(id)
        );
    }

    // ==========================================
    // ACTIVATE
    // ==========================================

    @PutMapping("/{id}/activate")
    public ResponseEntity<Product> activateProduct(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                service.activateProduct(id)
        );
    }
}