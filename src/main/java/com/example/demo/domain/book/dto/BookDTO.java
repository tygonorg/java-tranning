package com.example.demo.domain.book.dto;

import lombok.Data;
import java.util.List;

@Data
public class BookDTO {
    private String title;
    private String author;
    private Long categoryId;
    private List<Long> tagIds;
}
