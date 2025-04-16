package com.onlinebookstore.controller;

import static org.springframework.data.util.ReflectionUtils.setField;
import static org.springframework.http.HttpStatus.CREATED;
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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.lang.reflect.Field;
import java.util.List;
import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Book management", description = "Endpoints for managing books")
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/books")
public class BookController {
    private final BookService bookService;
    private final BookMapper bookMapper;

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping
    @Operation (summary = "Get all books",
            description = "Returns a paginated list of all books in the catalog.")
    public ResponseEntity<List<BookResponse>> getAll(Pageable pageable) {
        List<BookResponse> books = bookService.findAll(pageable).stream()
                .map(bookMapper::toDto)
                .toList();
        return ResponseEntity.status(OK).body(books);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping("/{id}")
    @Operation(summary = "Get book by id",
            description = "Returns a book by its unique identifier. Returns 404 if not found.")
    public ResponseEntity<BookResponse> getById(@PathVariable("id") Long id) {
        BookResponse bookResponse = bookMapper.toDto(bookService.getById(id));
        return ResponseEntity.status(OK).body(bookResponse);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping
    @Operation (summary = "Create new book",
            description = "Creates a new book and returns the saved book with it's ID.")
    public ResponseEntity<BookResponse> create(@RequestBody @Valid CreateBookRequest bookDto) {
        Book book = bookService.save(bookMapper.toModel(bookDto));
        BookResponse savedBook = bookMapper.toDto(book);
        return ResponseEntity.status(CREATED).body(savedBook);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @ResponseStatus(NO_CONTENT)
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete book by ID",
            description = "Deletes a book by it's ID. Returns 204 if deleted, 404 if not found."
    )
    public void deleteById(@PathVariable("id") Long id) {
        bookService.deleteById(id);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/{id}")
    @Operation(summary = "Update book by ID",
            description = "Updates an existing book's data. Fields with null values are ignored."
    )
    public ResponseEntity<BookResponse> updateById(@PathVariable("id") Long id,
                                                   @RequestBody CreateBookRequest bookDto) {
        Book book = bookMapper.toModel(bookDto);
        Book existingBook = bookService.getById(id);
        Book updatedBook = bookService.update(id, updatedBookFields(existingBook, book));
        return ResponseEntity.status(OK).body(bookMapper.toDto(updatedBook));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping("/search")
    @Operation(summary = "Search books by parameters",
            description = "Allows searching books using custom "
                    + "parameters like title, author."//, or price range
    )
    public ResponseEntity<List<BookResponse>> search(SearchParameters searchParameters) {
        List<BookResponse> books = bookService.search(searchParameters)
                .stream()
                .map(bookMapper::toDto)
                .toList();
        return ResponseEntity.status(OK).body(books);
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
}
