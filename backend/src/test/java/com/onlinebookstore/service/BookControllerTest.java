package com.onlinebookstore.service;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.onlinebookstore.controller.BookController;
import com.onlinebookstore.dto.BookResponse;
import com.onlinebookstore.dto.CreateBookRequest;
import com.onlinebookstore.mapper.BookMapper;
import com.onlinebookstore.repository.BookRepository;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import jakarta.persistence.EntityNotFoundException;
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
    private BookController bookController;
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private BookMapper bookMapper;

    @Test
    void createBookSuccessByController() {
        CreateBookRequest request = getCreateBookRequest("New Book", "John Doe", "ISBN 3322",
                BigDecimal.valueOf(19.99), "A description of the new book", "newbook.jpg");

        BookResponse response = bookController.create(request).getBody();

        assertThat(response).isNotNull();
        assertThat(response.getTitle()).isEqualTo(request.getTitle());
        assertThat(response.getAuthor()).isEqualTo(request.getAuthor());
        assertThat(response.getIsbn()).isEqualTo(request.getIsbn());
        assertThat(response.getPrice()).isEqualByComparingTo(request.getPrice());
        assertThat(response.getDescription()).isEqualTo(request.getDescription());
        assertThat(response.getCoverImage()).isEqualTo(request.getCoverImage());
    }

    @Test
    void createBookSuccess() {
        CreateBookRequest request = getCreateBookRequest("New Book", "John Doe", "ISBN 3322",
                BigDecimal.valueOf(19.99), "A description of the new book", "newbook.jpg");

        BookResponse response = getResponse(request);

        assertThat(response).isNotNull();
        assertThat(response.getTitle()).isEqualTo(request.getTitle());
        assertThat(response.getAuthor()).isEqualTo(request.getAuthor());
        assertThat(response.getIsbn()).isEqualTo(request.getIsbn());
        assertThat(response.getPrice()).isEqualByComparingTo(request.getPrice());
        assertThat(response.getDescription()).isEqualTo(request.getDescription());
        assertThat(response.getCoverImage()).isEqualTo(request.getCoverImage());
    }

    @Test
    void getAllBooksSuccess() {
        createTwoBooks();
        List<BookResponse> books = given()
                .when()
                .get("/api/v1/books")
                .then()
                .statusCode(200)
                .extract().jsonPath().getList("", BookResponse.class);

        assertThat(books).hasSize(2);
        assertThat(books.get(0).getTitle()).isEqualTo("Book 1");
        assertThat(books).extracting(BookResponse::getAuthor).contains("Author 2");
        assertThat(books).doesNotHaveDuplicates();
    }

    @Test
    void getBookByIdSuccess() {
        CreateBookRequest request = getCreateBookRequest("New Book", "John Doe", "ISBN 3322",
                BigDecimal.valueOf(19.99), "A description of the new book", "newbook.jpg");

        BookResponse createdBook = getResponse(request);

        BookResponse response = given()
                .when()
                .get("/api/v1/books/" + createdBook.getId())
                .then()
                .statusCode(200)
                .extract().as(BookResponse.class);

        assertThat(response).isNotNull();
        assertThat(response.getTitle()).isEqualTo(request.getTitle());
        assertThat(response.getAuthor()).isEqualTo(request.getAuthor());
        assertThat(response.getIsbn()).isEqualTo(request.getIsbn());
        assertThat(response.getPrice()).isEqualByComparingTo(request.getPrice());
        assertThat(response.getDescription()).isEqualTo(request.getDescription());
        assertThat(response.getCoverImage()).isEqualTo(request.getCoverImage());
    }

    @Test
    void getBookById_NotFound() {
        long id = 99999;
        given()
                .when()
                .get("/api/v1/books" + "/" + id)
                .then()
                .statusCode(404);

        assertThatThrownBy(() -> {
            throw new EntityNotFoundException("Can't find book by id" + id);
        })
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Can't find book by id" + id);
    }

    @BeforeEach
    void setup() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 8080;
        bookRepository.deleteAll();
    }

    private static BookResponse getResponse(CreateBookRequest request) {
        return given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/api/v1/books")
                .then()
                .statusCode(201)
                .extract().as(BookResponse.class);
    }

    private static void createTwoBooks() {
        CreateBookRequest book1 = getCreateBookRequest("Book 1", "Author 1", "ISBN1",
                BigDecimal.valueOf(15.99), "Description 1", "book1.jpg");
        CreateBookRequest book2 = getCreateBookRequest("Book 2", "Author 2", "ISBN2",
                BigDecimal.valueOf(25.99), "Description 2", "book2.jpg");

        createBook(book1);
        createBook(book2);
    }

    private static void createBook(CreateBookRequest book) {
        given()
                .contentType(ContentType.JSON)
                .body(book)
                .when()
                .post("/api/v1/books")
                .then()
                .statusCode(201);
    }

    private static CreateBookRequest getCreateBookRequest(String title, String author,
                                                          String isbn, BigDecimal price,
                                                          String description, String coverImage) {
        CreateBookRequest request = new CreateBookRequest();
        request.setTitle(title);
        request.setAuthor(author);
        request.setIsbn(isbn);
        request.setPrice(price);
        request.setDescription(description);
        request.setCoverImage(coverImage);
        return request;
    }
}
