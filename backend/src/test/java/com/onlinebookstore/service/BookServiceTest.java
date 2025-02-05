package com.onlinebookstore.service;

import com.onlinebookstore.model.Book;
import com.onlinebookstore.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.util.List;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    private BookServiceImpl bookService;

    @BeforeEach
    void setUp() {
        bookService = new BookServiceImpl(bookRepository);
    }

    @Test
    @DisplayName("Given a book, check if the book is saved to the repository")
    void isBookSaveToDB_save_true() {
        Book book = new Book();
        book.setId(1L);
        book.setTitle("New book");
        book.setAuthor("John Doe");
        book.setIsbn("ISBN 3322");
        book.setPrice(BigDecimal.valueOf(19.99));

        when(bookRepository.save(book)).thenReturn(book);

        Book savedBook = bookService.save(book);

        verify(bookRepository).save(book);

        assertEquals(book, savedBook);
    }

    @Test
    @DisplayName("Given a book, check if all books are retrieved from the repository")
    void isAllBooksRetrieved_findAll_true() {
        Book book1 = new Book();
        book1.setId(1L);
        book1.setTitle("Book 1");
        book1.setAuthor("John Doe");
        book1.setIsbn("ISBN 1111");
        book1.setPrice(BigDecimal.valueOf(14.99));

        Book book2 = new Book();
        book2.setId(2L);
        book2.setTitle("Book 2");
        book2.setAuthor("Jane Doe");
        book2.setIsbn("ISBN 2222");
        book2.setPrice(BigDecimal.valueOf(19.99));

        when(bookRepository.findAll()).thenReturn(List.of(book1, book2));

        List<Book> allBooks = bookService.findAll();

        verify(bookRepository).findAll();

        assertEquals(List.of(book1, book2), allBooks);
    }
}
