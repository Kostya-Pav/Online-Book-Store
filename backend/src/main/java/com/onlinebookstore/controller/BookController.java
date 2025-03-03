package com.onlinebookstore.controller;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;

import com.onlinebookstore.dto.BookResponse;
import com.onlinebookstore.dto.CreateBookRequest;
import com.onlinebookstore.mapper.BookMapper;
import com.onlinebookstore.model.Book;
import com.onlinebookstore.service.BookService;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
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
    public ResponseEntity<List<BookResponse>> getAll() {
        List<BookResponse> list = bookService.findAll().stream()
                .map(bookMapper::toDto)
                .toList();
        return ResponseEntity.status(OK).body(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookResponse> getById(@PathVariable("id") Long id) {
        try {
            BookResponse bookResponse = bookMapper.toDto(bookService.getBookById(id));
            return ResponseEntity.status(OK).body(bookResponse);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).build();
        }
    }

    @PostMapping
    public ResponseEntity<BookResponse> create(@RequestBody CreateBookRequest bookDto) {
        Book book = bookService.save(bookMapper.toModel(bookDto));
        BookResponse savedBook = bookMapper.toDto(book);
        return ResponseEntity.status(CREATED).body(savedBook);
    }
}
