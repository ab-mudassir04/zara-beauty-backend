package com.zara.backend.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.zara.backend.entity.Product;
import com.zara.backend.repository.ProductRepository;

@Service
public class ProductService {

    private final ProductRepository repository;

    public ProductService(ProductRepository repository) {
        this.repository = repository;
    }

    // ==========================================
    // GET ALL PRODUCTS
    // ==========================================

    public List<Product> getAllProducts() {
        return repository.findAll();
    }

    // ==========================================
    // GET ACTIVE PRODUCTS
    // ==========================================

    public List<Product> getActiveProducts() {
        return repository.findByActiveTrue();
    }

    // ==========================================
    // GET PRODUCT BY ID
    // ==========================================

    public Product getProductById(Long id) {

        if (id == null) {
            throw new RuntimeException("Product ID is required");
        }

        return repository.findById(id)
                .orElseThrow(
                        () -> new RuntimeException(
                                "Product not found with ID: " + id
                        )
                );
    }

    // ==========================================
    // CREATE PRODUCT
    // ==========================================

    public Product saveProduct(Product product) {

        validateProduct(product);

        product.setName(product.getName().trim());
        product.setCategory(product.getCategory().trim());

        if (product.getDescription() != null) {
            product.setDescription(
                    product.getDescription().trim()
            );
        }

        if (product.getImageUrl() != null) {
            product.setImageUrl(
                    product.getImageUrl().trim()
            );
        }

        if (product.getActive() == null) {
            product.setActive(true);
        }

        return repository.save(product);
    }

    // ==========================================
    // UPDATE PRODUCT
    // ==========================================

    public Product updateProduct(
            Long id,
            Product product
    ) {

        Product existing = repository.findById(id)
                .orElseThrow(
                        () -> new RuntimeException(
                                "Product not found with ID: " + id
                        )
                );

        validateProduct(product);

        existing.setName(
                product.getName().trim()
        );

        existing.setCategory(
                product.getCategory().trim()
        );

        existing.setPrice(
                product.getPrice()
        );

        existing.setStock(
                product.getStock()
        );

        existing.setImageUrl(
                product.getImageUrl()
        );

        existing.setDescription(
                product.getDescription()
        );

        existing.setRating(
                product.getRating()
        );

        /*
         * Do not overwrite createdAt.
         */

        if (product.getActive() != null) {
            existing.setActive(
                    product.getActive()
            );
        }

        return repository.save(existing);
    }

    // ==========================================
    // DELETE PRODUCT
    // ==========================================

    public void deleteProduct(Long id) {

        if (id == null) {
            throw new RuntimeException(
                    "Product ID is required"
            );
        }

        if (!repository.existsById(id)) {
            throw new RuntimeException(
                    "Product not found with ID: " + id
            );
        }

        repository.deleteById(id);
    }

    // ==========================================
    // DEACTIVATE PRODUCT
    // ==========================================

    public Product deactivateProduct(Long id) {

        Product product = getProductById(id);

        product.setActive(false);

        return repository.save(product);
    }

    // ==========================================
    // ACTIVATE PRODUCT
    // ==========================================

    public Product activateProduct(Long id) {

        Product product = getProductById(id);

        product.setActive(true);

        return repository.save(product);
    }

    // ==========================================
    // VALIDATION
    // ==========================================

    private void validateProduct(Product product) {

        if (product == null) {
            throw new RuntimeException(
                    "Product data is required"
            );
        }

        // --------------------------------------
        // NAME
        // --------------------------------------

        if (product.getName() == null ||
                product.getName().trim().isEmpty()) {

            throw new RuntimeException(
                    "Product name is required"
            );
        }

        // --------------------------------------
        // CATEGORY
        // --------------------------------------

        if (product.getCategory() == null ||
                product.getCategory().trim().isEmpty()) {

            throw new RuntimeException(
                    "Product category is required"
            );
        }

        // --------------------------------------
        // PRICE
        // --------------------------------------

        if (product.getPrice() == null ||
                product.getPrice() <= 0) {

            throw new RuntimeException(
                    "Price must be greater than 0"
            );
        }

        // --------------------------------------
        // STOCK
        // --------------------------------------

        if (product.getStock() == null) {

            product.setStock(0);

        } else if (product.getStock() < 0) {

            throw new RuntimeException(
                    "Stock cannot be negative"
            );
        }

        // --------------------------------------
        // RATING
        // --------------------------------------

        if (product.getRating() == null) {

            product.setRating(0.0);

        } else if (
                product.getRating() < 0 ||
                product.getRating() > 5
        ) {

            throw new RuntimeException(
                    "Rating must be between 0 and 5"
            );
        }

        // --------------------------------------
        // IMAGE
        // --------------------------------------

        if (product.getImageUrl() != null &&
                product.getImageUrl().length() > 10_000_000) {

            throw new RuntimeException(
                    "Image data is too large"
            );
        }
    }
}