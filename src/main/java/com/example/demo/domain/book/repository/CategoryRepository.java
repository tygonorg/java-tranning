package com.example.demo.domain.book.repository;

import com.example.demo.domain.book.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
