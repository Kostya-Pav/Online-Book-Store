package com.onlinebookstore;

import com.onlinebookstore.model.Book;
import io.restassured.RestAssured;
import java.math.BigDecimal;
import java.util.function.Consumer;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class BaseTest {

    @LocalServerPort
    protected int port;

    @BeforeEach
    void setupRestAssured() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
    }

    protected Book bookTemplate(Consumer<Book> modifier) {
        Book book = new Book();
        book.setTitle("Book");
        book.setAuthor("Author");
        book.setIsbn("ISBN1");
        book.setPrice(BigDecimal.valueOf(15.99));
        book.setDescription("Description");
        book.setCoverImage("book.jpg");

        modifier.accept(book);
        return book;
    }
}
