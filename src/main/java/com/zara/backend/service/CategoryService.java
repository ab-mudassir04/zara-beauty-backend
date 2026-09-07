package com.zara.backend.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zara.backend.dto.CategoryResponse;
import com.zara.backend.entity.Category;
import com.zara.backend.repository.CategoryRepository;
import com.zara.backend.repository.ProductRepository;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    public CategoryService(
            CategoryRepository categoryRepository,
            ProductRepository productRepository
    ) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
    }

    // ==========================================
    // GET ALL CATEGORIES
    // ==========================================

    public List<CategoryResponse> getAllCategories() {

        return categoryRepository.findAll()
                .stream()
                .map(category -> {

                    long productCount =
                            productRepository.countByCategoryIgnoreCase(
                                    category.getName()
                            );

                    return new CategoryResponse(
                            category.getId(),
                            category.getName(),
                            category.getActive(),
                            productCount
                    );
                })
                .collect(Collectors.toList());
    }

    // ==========================================
    // GET CATEGORY BY ID
    // ==========================================

    public Category getCategoryById(Long id) {

        return categoryRepository
                .findById(id)
                .orElseThrow(
                        () -> new RuntimeException(
                                "Category not found with id: " + id
                        )
                );
    }

    // ==========================================
    // CREATE CATEGORY
    // ==========================================

    public Category createCategory(Category category) {

        if (category.getName() == null ||
                category.getName().trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Category name is required."
            );
        }

        String categoryName =
                category.getName().trim();

        if (categoryRepository
                .existsByNameIgnoreCase(categoryName)) {

            throw new IllegalArgumentException(
                    "Category already exists."
            );
        }

        category.setName(categoryName);

        if (category.getActive() == null) {
            category.setActive(true);
        }

        return categoryRepository.save(category);
    }

    // ==========================================
    // UPDATE CATEGORY
    // ==========================================

    public Category updateCategory(
            Long id,
            Category category
    ) {

        Category existing =
                getCategoryById(id);

        if (category.getName() == null ||
                category.getName().trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Category name is required."
            );
        }

        String newName =
                category.getName().trim();

        if (!existing.getName().equalsIgnoreCase(newName)
                && categoryRepository
                    .existsByNameIgnoreCase(newName)) {

            throw new IllegalArgumentException(
                    "Category already exists."
            );
        }

        existing.setName(newName);

        if (category.getActive() != null) {
            existing.setActive(
                    category.getActive()
            );
        }

        return categoryRepository.save(existing);
    }

    // ==========================================
    // DELETE CATEGORY
    // ==========================================

    @Transactional
    public void deleteCategory(Long id) {

        Category category =
                getCategoryById(id);

        /*
         * IMPORTANT:
         *
         * This deletes ONLY the category.
         *
         * It does NOT delete products.
         */

        categoryRepository.delete(category);
    }
}