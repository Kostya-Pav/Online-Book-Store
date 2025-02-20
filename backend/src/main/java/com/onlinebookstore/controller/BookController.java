package com.onlinebookstore.controller;

import com.onlinebookstore.dto.BookResponse;
import com.onlinebookstore.dto.CreateBookRequest;
import com.onlinebookstore.mapper.BookMapper;
import com.onlinebookstore.service.BookService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/books")
public class BookController {
    private final BookService bookService;
    private final BookMapper bookMapper;

    @GetMapping
    public List<BookResponse> getAll() {
        return bookService.findAll().stream()
                .map(bookMapper::toDto)
                .toList();
    }

    @GetMapping("/{id}")
    public BookResponse getById(@PathVariable("id") Long id) {
        return bookMapper.toDto(bookService.getBookById(id));
    }

    @PostMapping
    public ResponseEntity<BookResponse> create(@RequestBody CreateBookRequest bookDto) {
        BookResponse savedBook = bookMapper.toDto(bookService.save(bookDto));
        return ResponseEntity.status(HttpStatus.CREATED).body(savedBook);
    }
}
