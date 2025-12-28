package com.example.demo.domain.book.repository;

import com.example.demo.domain.book.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepository extends JpaRepository<Tag, Long> {
}
