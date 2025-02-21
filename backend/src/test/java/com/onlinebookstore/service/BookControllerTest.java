package com.onlinebookstore.service;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

import com.onlinebookstore.dto.BookResponse;
import com.onlinebookstore.dto.CreateBookRequest;
import com.onlinebookstore.mapper.BookMapper;
import com.onlinebookstore.repository.BookRepository;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@RequiredArgsConstructor
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestPropertySource(locations = "classpath:application-test.properties")
class BookControllerTest {

    @Autowired
    private BookRepository bookRepository;
    private BookMapper bookMapper;

    @Test
    void createBook_Success() {
        CreateBookRequest request = new CreateBookRequest();
        request.setTitle("New Book");
        request.setAuthor("John Doe");
        request.setIsbn("ISBN 3322");
        request.setPrice(BigDecimal.valueOf(19.99));
        request.setDescription("A description of the new book");
        request.setCoverImage("newbook.jpg");

        BookResponse response = given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/api/v1/books")
                .then().log().all()
                .statusCode(201)
                .extract().as(BookResponse.class);

        assertThat(response).isNotNull();
        assertThat(response.getTitle()).isEqualTo(request.getTitle());
    }

    @Test
    void getAllBooks_Success() {
        List<BookResponse> books = given()
                .when()
                .get("/api/v1/books")
                .then()
                .statusCode(200)
                .extract().jsonPath().getList("", BookResponse.class);

        assertThat(books).isNotEmpty();
    }

    @Test
    void getBookById_Success() {
        CreateBookRequest request = new CreateBookRequest();
        request.setTitle("Book for Testing");
        request.setAuthor("John Doe");
        request.setIsbn("ISBN 1234");
        request.setPrice(BigDecimal.valueOf(25.99));
        request.setDescription("Test book description");
        request.setCoverImage("testbook.jpg");

        BookResponse createdBook = given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/api/v1/books")
                .then()
                .statusCode(201)
                .extract().as(BookResponse.class);

        Long createdBookId = createdBook.getId();

        BookResponse response = given()
                .when()
                .get("/api/v1/books/" + createdBookId)
                .then()
                .statusCode(200)
                .extract().as(BookResponse.class);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(createdBookId);
        assertThat(response.getTitle()).isEqualTo(createdBook.getTitle());
    }

    @Test
    void getBookById_NotFound() {
        given()
                .when()
                .get("/api/v1/books" + "/99999")
                .then()
                .statusCode(404);
    }

    @BeforeEach
    void setup() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 8080;
        bookRepository.deleteAll();
        CreateBookRequest book1 = new CreateBookRequest();
        book1.setTitle("Book 1");
        book1.setAuthor("Author 1");
        book1.setIsbn("ISBN1");
        book1.setPrice(BigDecimal.valueOf(15.99));
        book1.setDescription("Description 1");
        book1.setCoverImage("book1.jpg");

        CreateBookRequest book2 = new CreateBookRequest();
        book2.setTitle("Book 2");
        book2.setAuthor("Author 2");
        book2.setIsbn("ISBN2");
        book2.setPrice(BigDecimal.valueOf(25.99));
        book2.setDescription("Description 2");
        book2.setCoverImage("book2.jpg");

        given()
                .contentType(ContentType.JSON)
                .body(book1)
                .when()
                .post("/api/v1/books")
                .then().log().all()
                .statusCode(201);

        given()
                .contentType(ContentType.JSON)
                .body(book2)
                .when()
                .post("/api/v1/books")
                .then().log().all()
                .statusCode(201);
    }
}
