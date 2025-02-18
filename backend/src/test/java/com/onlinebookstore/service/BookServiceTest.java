package com.onlinebookstore.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.onlinebookstore.dto.BookDto;
import com.onlinebookstore.dto.CreateBookRequestDto;
import com.onlinebookstore.exception.EntityNotFoundException;
import com.onlinebookstore.mapper.BookMapper;
import com.onlinebookstore.model.Book;
import com.onlinebookstore.repository.BookRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {
    @Mock
    private BookRepository bookRepository;

    @Mock
    private BookMapper bookMapper;

    @InjectMocks
    private BookServiceImpl bookService;
    private Book book;
    private BookDto bookDto;
    private CreateBookRequestDto createBookRequestDto;

    @BeforeEach
    void setUp() {
        createBookRequestDto = new CreateBookRequestDto();
        createBookRequestDto.setTitle("New book");
        createBookRequestDto.setAuthor("John Doe");
        createBookRequestDto.setIsbn("ISBN 3322");
        createBookRequestDto.setPrice(BigDecimal.valueOf(19.99));
        createBookRequestDto.setDescription("Description");
        createBookRequestDto.setCoverImage("New Image");

        book = new Book();
        book.setId(1L);
        book.setTitle("New book");
        book.setAuthor("John Doe");
        book.setIsbn("ISBN 3322");
        book.setPrice(BigDecimal.valueOf(19.99));
        book.setDescription("Description");
        book.setCoverImage("New Image");

        bookDto = new BookDto();
        bookDto.setId(1L);
        bookDto.setTitle("New book");
        bookDto.setAuthor("John Doe");
        bookDto.setIsbn("ISBN 3322");
        bookDto.setPrice(BigDecimal.valueOf(19.99));
        bookDto.setDescription("Description");
        bookDto.setCoverImage("New Image");
    }

    @Test
    @DisplayName("Should save book and return BookDto")
    void saveBook_Success() {
        when(bookMapper.toModel(createBookRequestDto)).thenReturn(book);
        when(bookRepository.save(book)).thenReturn(book);
        when(bookMapper.toDto(book)).thenReturn(bookDto);
        BookDto savedBook = bookService.save(createBookRequestDto);
        assertNotNull(savedBook);
        assertEquals(bookDto, savedBook);
    }

    @Test
    @DisplayName("Should retrieve all books and return list of BookDto")
    void findAllBooks_Success() {
        when(bookRepository.findAll()).thenReturn(List.of(book));
        when(bookMapper.toDto(book)).thenReturn(bookDto);
        List<BookDto> books = bookService.findAll();
        assertNotNull(books);
        assertEquals(1, books.size());
        assertEquals(bookDto, books.get(0));
    }

    @Test
    @DisplayName("Should return BookDto when book is found by id")
    void getBookById_Success() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(bookMapper.toDto(book)).thenReturn(bookDto);
        BookDto foundBook = bookService.getBookById(1L);
        assertNotNull(foundBook);
        assertEquals(bookDto, foundBook);
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when book is not found by id")
    void getBookById_NotFound() {
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());
        Exception exception = assertThrows(EntityNotFoundException.class,
                () -> bookService.getBookById(1L));
        assertEquals("Can't find book by id1", exception.getMessage());
    }
}
