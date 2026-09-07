package com.zara.backend.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
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

import com.zara.backend.dto.CategoryResponse;
import com.zara.backend.entity.Category;
import com.zara.backend.service.CategoryService;

@RestController
@RequestMapping("/categories")
@CrossOrigin(origins = "http://localhost:5173")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(
            CategoryService categoryService
    ) {
        this.categoryService = categoryService;
    }

    // ==========================================
    // GET ALL CATEGORIES
    // ==========================================

    @GetMapping
    public ResponseEntity<List<CategoryResponse>>
    getCategories() {

        return ResponseEntity.ok(
                categoryService.getAllCategories()
        );
    }

    // ==========================================
    // GET CATEGORY BY ID
    // ==========================================

    @GetMapping("/{id}")
    public ResponseEntity<?> getCategory(
            @PathVariable Long id
    ) {

        try {

            return ResponseEntity.ok(
                    categoryService.getCategoryById(id)
            );

        } catch (RuntimeException e) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }

    // ==========================================
    // CREATE CATEGORY
    // ==========================================

    @PostMapping
    public ResponseEntity<?> createCategory(
            @RequestBody Category category
    ) {

        try {

            Category savedCategory =
                    categoryService.createCategory(category);

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(savedCategory);

        } catch (IllegalArgumentException e) {

            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(e.getMessage());
        }
    }

    // ==========================================
    // UPDATE CATEGORY
    // ==========================================

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCategory(
            @PathVariable Long id,
            @RequestBody Category category
    ) {

        try {

            Category updatedCategory =
                    categoryService.updateCategory(
                            id,
                            category
                    );

            return ResponseEntity.ok(
                    updatedCategory
            );

        } catch (IllegalArgumentException e) {

            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(e.getMessage());

        } catch (RuntimeException e) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }

    // ==========================================
    // DELETE CATEGORY
    // ==========================================

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCategory(
            @PathVariable Long id
    ) {

        try {

            categoryService.deleteCategory(id);

            return ResponseEntity.ok(
                    "Category deleted successfully."
            );

        } catch (RuntimeException e) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }
}