package com.example.demo.domain.book.service;

import com.example.demo.domain.book.dto.BookDTO;
import com.example.demo.domain.book.entity.Book;

import java.util.List;

public interface BookService {
    Book addBook(BookDTO bookDTO);

    Book updateBook(Long id, BookDTO bookDTO);

    void deleteBook(Long id);

    List<Book> getAllBooks();

    Book getBookById(Long id);
}
