package com.onlinebookstore.service;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.onlinebookstore.BaseTest;
import com.onlinebookstore.dto.BookResponse;
import com.onlinebookstore.dto.CreateBookRequest;
import com.onlinebookstore.model.Book;
import com.onlinebookstore.repository.BookRepository;
import io.restassured.common.mapper.TypeRef;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;

@RequiredArgsConstructor
@TestPropertySource(locations = "classpath:application-test.properties")
class BookControllerTest extends BaseTest {
    @Autowired
    private BookRepository bookRepository;

    private final List<Long> createdBookIds = new ArrayList<>();

    @AfterEach
    void tearDown() {
        bookRepository.deleteAllById(createdBookIds);
        createdBookIds.clear();
    }

    @Test
    void callCreateBookEndpointSuccess() {
        CreateBookRequest request = getCallCreateBookEndpointRequest("New Book", "John Doe",
                "ISBN 3322", BigDecimal.valueOf(19.99), "A description of the new book",
                "newbook.jpg");

        Response response = given()
                .contentType(ContentType.JSON)
                .body(request)
                .post("/api/v1/books");

        assertEquals(201, response.getStatusCode(),
                "Controller should respond with HttpStatus.CREATED");

        BookResponse bookResponse = response.body().as(BookResponse.class);

        createdBookIds.add(bookResponse.getId());

        Book savedBook = bookRepository.findById(bookResponse.getId()).orElseThrow(
                () -> new EntityNotFoundException("Can't find book by id" + bookResponse.getId())
        );

        assertThat(bookResponse.getId()).isEqualTo(savedBook.getId());
        assertThat(bookResponse).isNotNull();
        assertThat(bookResponse.getTitle()).isEqualTo(request.getTitle());
        assertThat(bookResponse.getAuthor()).isEqualTo(request.getAuthor());
        assertThat(bookResponse.getIsbn()).isEqualTo(request.getIsbn());
        assertThat(bookResponse.getPrice()).isEqualByComparingTo(request.getPrice());
        assertThat(bookResponse.getDescription()).isEqualTo(request.getDescription());
        assertThat(bookResponse.getCoverImage()).isEqualTo(request.getCoverImage());
    }

    @Test
    void getAllBooksSuccess() {
        Book book1ToSave = bookTemplate(book -> {
            book.setTitle("Book 2");
            book.setIsbn("ISBN2");
        });
        Book book2ToSave = bookTemplate(book -> {
            book.setTitle("Book 3");
            book.setIsbn("ISBN3");
        });
        Book savedBook1 = bookRepository.save(book1ToSave);
        Book savedBook2 = bookRepository.save(book2ToSave);

        createdBookIds.add(savedBook1.getId());
        createdBookIds.add(savedBook2.getId());

        Response response = given()
                .get("/api/v1/books");

        assertEquals(200, response.getStatusCode(), "Controller should respond with HttpStatus.OK");

        List<BookResponse> books = response.body().as(new TypeRef<List<BookResponse>>() {});

        assertThat(books).hasSize(2);
        assertThat(books.get(0).getTitle()).isEqualTo("Book 2");
        assertThat(books).extracting(BookResponse::getAuthor).contains("Author");
        assertThat(books).doesNotHaveDuplicates();
    }

    @Test
    void getBookByIdSuccess() {
        Book bookToSave = bookTemplate(book -> {});
        Book savedBook = bookRepository.save(bookToSave);

        Response response = given()
                .get("/api/v1/books/" + savedBook.getId());

        assertEquals(200, response.getStatusCode(), "Controller should respond with HttpStatus.OK");

        BookResponse book = response.body().as(new TypeRef<BookResponse>() {});

        createdBookIds.add(book.getId());

        assertThat(book).isNotNull();
        assertThat(book.getTitle()).isEqualTo(savedBook.getTitle());
        assertThat(book.getAuthor()).isEqualTo(savedBook.getAuthor());
        assertThat(book.getIsbn()).isEqualTo(savedBook.getIsbn());
        assertThat(book.getPrice()).isEqualByComparingTo(savedBook.getPrice());
        assertThat(book.getDescription()).isEqualTo(savedBook.getDescription());
        assertThat(book.getCoverImage()).isEqualTo(savedBook.getCoverImage());
    }

    @Test
    void getBookById_NotFound() {
        long id = Long.MAX_VALUE;
        Response response = given()
                .get("/api/v1/books" + "/" + id);

        assertEquals(404, response.getStatusCode(),
                "Controller should respond with HttpStatus.NOT_FOUND");
    }

    private CreateBookRequest getCallCreateBookEndpointRequest(String title, String author,
                                                               String isbn, BigDecimal price,
                                                               String descr, String coverImage) {
        CreateBookRequest request = new CreateBookRequest();
        request.setTitle(title);
        request.setAuthor(author);
        request.setIsbn(isbn);
        request.setPrice(price);
        request.setDescription(descr);
        request.setCoverImage(coverImage);
        return request;
    }
}
