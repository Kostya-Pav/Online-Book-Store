package com.onlinebookstore.service;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

import com.onlinebookstore.dto.BookResponse;
import com.onlinebookstore.dto.CreateBookRequest;
import com.onlinebookstore.model.Book;
import com.onlinebookstore.repository.BookRepository;
import io.restassured.http.ContentType;
import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BookControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private BookRepository bookRepository;

    @BeforeEach
    void setup() {
        Book book = new Book();
        book.setId(1L);
        book.setTitle("New book");
        book.setAuthor("John Doe");
        book.setIsbn("ISBN 3322");
        book.setPrice(BigDecimal.valueOf(19.99));
        book.setDescription("Description");
        book.setCoverImage("New Image");
        bookRepository.save(book);
    }

    /**
     * Тест на создание новой книги.
     * Отправляет POST-запрос с JSON-данными и проверяет, что книга успешно создана.
     */
    @Test
    void createBook_Success() {
        CreateBookRequest request = new CreateBookRequest();
        request.setTitle("New book");
        request.setAuthor("John Doe");
        request.setIsbn("ISBN 3322");
        request.setPrice(BigDecimal.valueOf(19.99));
        request.setDescription("Description");
        request.setCoverImage("New Image");

        BookResponse response = given()
                .port(port)
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/books")
                .then()
                .statusCode(201)
                .extract().as(BookResponse.class);

        assertThat(response).isNotNull();
        assertThat(response.getTitle()).isEqualTo(request.getTitle());
        assertThat(response.getAuthor()).isEqualTo(request.getAuthor());
    }

    /**
     * Тест на получение списка всех книг.
     * Отправляет GET-запрос и проверяет, что список не пустой.
     */
    @Test
    void getAllBooks_Success() {
        List<BookResponse> books = given()
                .port(port)
                .when()
                .get("/api/v1/books")
                .then()
                .statusCode(200)
                .extract().jsonPath().getList("", BookResponse.class);

        assertThat(books).isNotNull().isNotEmpty();
    }

    /**
     * Тест на получение книги по ID.
     * Отправляет GET-запрос с существующим ID и проверяет, что книга найдена.
     */
    @Test
    void getBookById_Success() {
        Long bookId = 1L; // Используй существующий ID

        BookResponse response = given()
                .port(port)
                .when()
                .get("/api/v1/books{id}", bookId)
                .then()
                .statusCode(200)
                .extract().as(BookResponse.class);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(bookId);
    }

    /**
     * Тест на ошибку при получении несуществующей книги.
     * Отправляет GET-запрос с несуществующим ID и ожидает 404 Not Found.
     */
    @Test
    void getBookById_NotFound() {
        Long nonExistentId = 999L;

        given()
                .port(port)
                .when()
                .get("/api/v1/books{id}", nonExistentId)
                .then()
                .statusCode(404);
    }
}
