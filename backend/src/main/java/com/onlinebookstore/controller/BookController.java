package com.onlinebookstore.controller;

import static org.springframework.data.util.ReflectionUtils.setField;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.util.ReflectionUtils.getField;
import static org.springframework.util.ReflectionUtils.makeAccessible;

import com.onlinebookstore.dto.BookResponse;
import com.onlinebookstore.dto.CreateBookRequest;
import com.onlinebookstore.mapper.BookMapper;
import com.onlinebookstore.model.Book;
import com.onlinebookstore.model.SearchParameters;
import com.onlinebookstore.service.BookService;
import jakarta.persistence.EntityNotFoundException;
import java.lang.reflect.Field;
import java.util.List;
import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/books")
public class BookController {
    private final BookService bookService;
    private final BookMapper bookMapper;

    @GetMapping
    public ResponseEntity<List<BookResponse>> getAll() {
        List<BookResponse> books = bookService.findAll().stream()
                .map(bookMapper::toDto)
                .toList();
        return ResponseEntity.status(OK).body(books);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookResponse> getById(@PathVariable("id") Long id) {
        try {
            BookResponse bookResponse = bookMapper.toDto(bookService.getById(id));
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

    @ResponseStatus(NO_CONTENT)
    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable("id") Long id) {
        bookService.deleteById(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BookResponse> updateById(@PathVariable("id") Long id,
                                                   @RequestBody CreateBookRequest bookDto) {
        Book book = bookMapper.toModel(bookDto);
        Book existingBook = bookService.getById(id);
        Book updatedBook = bookService.update(id, updatedBookFields(existingBook, book));
        return ResponseEntity.status(OK).body(bookMapper.toDto(updatedBook));
    }

    private Consumer<Book> updatedBookFields(Book existingBook, Book book) {
        return newBook -> {
            Field[] fields = Book.class.getDeclaredFields();

            for (Field field : fields) {
                makeAccessible(field);
                Object existingValue = getField(field, existingBook);
                Object newValue = getField(field, book);

                if (newValue != null && !newValue.equals(existingValue)) {
                    setField(field, newBook, newValue);
                }
            }
        };
    }

    @GetMapping("/search")
    public ResponseEntity<List<BookResponse>> search(SearchParameters searchParameters) {
        List<BookResponse> books = bookService.search(searchParameters)
                .stream()
                .map(bookMapper::toDto)
                .toList();
        return ResponseEntity.status(OK).body(books);
    }
}
