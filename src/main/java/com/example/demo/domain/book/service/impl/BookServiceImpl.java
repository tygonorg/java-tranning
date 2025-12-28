package com.example.demo.domain.book.service.impl;

import com.example.demo.domain.book.dto.BookDTO;
import com.example.demo.domain.book.entity.Book;
import com.example.demo.domain.book.entity.Category;
import com.example.demo.domain.book.entity.Tag;
import com.example.demo.domain.book.repository.BookRepository;
import com.example.demo.domain.book.repository.CategoryRepository;
import com.example.demo.domain.book.repository.TagRepository;
import com.example.demo.domain.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import com.example.demo.domain.book.service.BookService;

@Service
@RequiredArgsConstructor
@Transactional
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;
    private final CategoryRepository categoryRepository;
    private final TagRepository tagRepository;

    @Override
    public Book addBook(BookDTO bookDTO) {
        Book book = new Book();
        book.setTitle(bookDTO.getTitle());
        book.setAuthor(bookDTO.getAuthor());

        if (bookDTO.getCategoryId() != null) {
            Category category = categoryRepository.findById(bookDTO.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Category not found"));
            book.setCategory(category);
        }

        if (bookDTO.getTagIds() != null && !bookDTO.getTagIds().isEmpty()) {
            List<Tag> tags = tagRepository.findAllById(bookDTO.getTagIds());
            book.setTags(tags);
        }

        return bookRepository.save(book);
    }

    @Override
    public Book updateBook(Long id, BookDTO bookDTO) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + id));

        book.setTitle(bookDTO.getTitle());
        book.setAuthor(bookDTO.getAuthor());

        if (bookDTO.getCategoryId() != null) {
            Category category = categoryRepository.findById(bookDTO.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
            book.setCategory(category);
        }

        if (bookDTO.getTagIds() != null && !bookDTO.getTagIds().isEmpty()) {
            List<Tag> tags = tagRepository.findAllById(bookDTO.getTagIds());
            book.setTags(tags);
        }

        return bookRepository.save(book);
    }

    @Override
    public void deleteBook(Long id) {
        bookRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Book getBookById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Book not found with id: " + id));
    }
}
