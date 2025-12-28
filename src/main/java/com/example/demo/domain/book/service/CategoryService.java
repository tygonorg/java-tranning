package com.example.demo.domain.book.service;

import com.example.demo.domain.book.dto.CategoryDTO;
import com.example.demo.domain.book.entity.Category;

import java.util.List;

public interface CategoryService {
    Category createCategory(CategoryDTO categoryDTO);

    Category updateCategory(Long id, CategoryDTO categoryDTO);

    void deleteCategory(Long id);

    List<Category> getAllCategories();

    Category getCategoryById(Long id);
}
