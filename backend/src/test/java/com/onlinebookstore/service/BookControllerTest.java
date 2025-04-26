package com.onlinebookstore.service;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.onlinebookstore.BaseTest;
import com.onlinebookstore.dto.BookResponse;
import com.onlinebookstore.dto.CreateBookRequest;
import com.onlinebookstore.model.Book;
import com.onlinebookstore.model.Role;
import com.onlinebookstore.model.RoleName;
import com.onlinebookstore.model.User;
import com.onlinebookstore.repository.book.BookRepository;
import com.onlinebookstore.repository.user.RoleRepository;
import com.onlinebookstore.repository.user.UserRepository;
import io.restassured.common.mapper.TypeRef;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;

@RequiredArgsConstructor
@TestPropertySource(locations = "classpath:application-test.properties")
class BookControllerTest extends BaseTest {
    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final List<Long> createdBookIds = new ArrayList<>();

    @BeforeEach
    void setupAdminUser() {
        Role adminRole = roleRepository.findByName(RoleName.ADMIN)
                .orElseGet(() -> roleRepository.save(new Role(RoleName.ADMIN)));

        if (userRepository.findByEmail("admin@example.com").isEmpty()) {
            User admin = new User();
            admin.setEmail("admin@example.com");
            admin.setPassword(passwordEncoder.encode("admin1234"));
            admin.setFirstName("Admin");
            admin.setLastName("User");
            admin.setShippingAddress("Some address");
            admin.getRoles().add(adminRole);
            userRepository.save(admin);
        }
    }

    @AfterEach
    void tearDown() {
        bookRepository.deleteAllById(createdBookIds);
        createdBookIds.clear();
    }

    @Test
    void callCreateBookEndpointSuccess() {
        CreateBookRequest request = getCallCreateBookEndpointRequest("New Book", "John Doe",
                "978-3-16-148410-0", BigDecimal.valueOf(19.99), "A description of the new book",
                "newbook.jpg");

        Response response = given()
                .contentType(ContentType.JSON)
                .auth().preemptive().basic("admin@example.com", "admin1234")
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
    void createBookWhenTitleNullShouldFail() {
        CreateBookRequest request = new CreateBookRequest();
        request.setAuthor("John Doe");
        request.setIsbn("isbn");
        request.setPrice(BigDecimal.valueOf(15.25));

        Response response = given()
                .contentType(ContentType.JSON)
                .auth().preemptive().basic("admin@example.com", "admin1234")
                .body(request)
                .post("/api/v1/books");

        assertEquals(400, response.getStatusCode(),
                "Controller should respond with HttpStatus.BAD_REQUEST");
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
                .auth().preemptive().basic("admin@example.com", "admin1234")
                .get("/api/v1/books");

        assertEquals(200, response.getStatusCode(), "Controller should respond with HttpStatus.OK");

        List<BookResponse> books = response.body().as(new TypeRef<List<BookResponse>>() {
        });

        assertThat(books).hasSize(2);
        assertThat(books.get(0).getTitle()).isEqualTo("Book 2");
        assertThat(books).extracting(BookResponse::getAuthor).contains("Author");
        assertThat(books).doesNotHaveDuplicates();
    }

    @Test
    void getBookByIdSuccess() {
        Book bookToSave = bookTemplate(book -> {
        });
        Book savedBook = bookRepository.save(bookToSave);

        Response response = given()
                .auth().preemptive().basic("admin@example.com", "admin1234")
                .get("/api/v1/books/" + savedBook.getId());

        assertEquals(200, response.getStatusCode(), "Controller should respond with HttpStatus.OK");

        BookResponse book = response.body().as(new TypeRef<BookResponse>() {
        });

        createdBookIds.add(book.getId());

        assertThat(book).isNotNull();
        assertThat(book.getTitle()).isEqualTo(savedBook.getTitle());
        assertThat(book.getAuthor()).isEqualTo(savedBook.getAuthor());
        assertThat(book.getIsbn()).isEqualTo(savedBook.getIsbn());
        assertThat(book.getPrice()).isEqualTo(savedBook.getPrice());
        assertThat(book.getDescription()).isEqualTo(savedBook.getDescription());
        assertThat(book.getCoverImage()).isEqualTo(savedBook.getCoverImage());
    }

    @Test
    void getBookById_NotFound() {
        long id = Long.MAX_VALUE;
        Response response = given()
                .auth().preemptive().basic("admin@example.com", "admin1234")
                .get("/api/v1/books" + "/" + id);

        assertEquals(404, response.getStatusCode(),
                "Controller should respond with HttpStatus.NOT_FOUND");
    }

    @Test
    void deleteByIdSuccess() {
        Book bookToSave = bookTemplate(book -> {
            book.setIsbn("ISBN11");
        });
        Book savedBook = bookRepository.save(bookToSave);
        createdBookIds.add(savedBook.getId());

        Response response = given()
                .auth().preemptive().basic("admin@example.com", "admin1234")
                .delete("/api/v1/books" + "/" + savedBook.getId());

        assertEquals(404, response.getStatusCode(),
                "Controller should respond with HttpStatus.NO_CONTENT");

        assertThat(bookRepository.existsById(savedBook.getId())).isFalse();
    }

    @Test
    void deleteByIdWhenBookDoesNotExistShouldReturnNotFound() {
        Response response = given()
                .auth().preemptive().basic("admin@example.com", "admin1234")
                .delete("/api/v1/books/" + Integer.MAX_VALUE);

        assertEquals(404, response.getStatusCode(),
                "Controller should respond with HttpStatus.NOT_FOUND");
    }

    @Test
    void updateByIdSuccess() {
        Book bookToSave = bookTemplate(book -> {
            book.setIsbn("ISBN10");
        });
        Book savedBook = bookRepository.save(bookToSave);
        createdBookIds.add(savedBook.getId());

        CreateBookRequest request = getCallCreateBookEndpointRequest("Updated Book", "New Author",
                "ISBN4", BigDecimal.valueOf(29.99), "A new description of the updated book",
                "updatedbook.jpg");

        Response response = given()
                .contentType(ContentType.JSON)
                .auth().preemptive().basic("admin@example.com", "admin1234")
                .body(request)
                .put("/api/v1/books" + "/" + savedBook.getId());

        assertEquals(200, response.getStatusCode(), "Controller should respond with HttpStatus.OK");

        BookResponse updatedBook = response.body().as(BookResponse.class);

        assertThat(updatedBook.getTitle()).isEqualTo(request.getTitle());
        assertThat(updatedBook.getAuthor()).isEqualTo(request.getAuthor());
        assertThat(updatedBook.getIsbn()).isEqualTo(request.getIsbn());
        assertThat(updatedBook.getPrice()).isEqualTo(request.getPrice());
        assertThat(updatedBook.getDescription()).isEqualTo(request.getDescription());
        assertThat(updatedBook.getCoverImage()).isEqualTo(request.getCoverImage());
    }

    @Test
    void searchBookByParamsSuccess() {
        Book bookToSave1 = bookTemplate(book -> {
            book.setTitle("Java Basics");
            book.setAuthor("John Doe");
            book.setIsbn("ISBN123");
            book.setPrice(BigDecimal.valueOf(19.99));
        });

        Book bookToSave2 = bookTemplate(book -> {
            book.setTitle("Spring Boot Guide");
            book.setAuthor("Jane Smith");
            book.setIsbn("ISBN456");
            book.setPrice(BigDecimal.valueOf(25.49));
        });

        Book savedBook1 = bookRepository.save(bookToSave1);
        createdBookIds.add(savedBook1.getId());

        Book savedBook2 = bookRepository.save(bookToSave2);
        createdBookIds.add(savedBook2.getId());

        Response response = given()
                .auth().preemptive().basic("admin@example.com", "admin1234")
                .param("title", "Java Basics")
                .param("author", "John Doe")
                .param("isbn", "ISBN123")
                .contentType(ContentType.JSON)
                .get("/api/v1/books" + "/search");

        assertEquals(200, response.getStatusCode(), "Controller should respond with HttpStatus.OK");

        List<BookResponse> books = response.body().as(new TypeRef<List<BookResponse>>() {
        });

        assertThat(books).extracting(BookResponse::getTitle).containsOnly(savedBook1.getTitle());
        assertThat(books).hasSize(1);
    }

    @Test
    void searchBookByParamWhenInvalidSortParamShouldIgnoreParam() {
        Book bookToSave1 = bookTemplate(book -> {
            book.setTitle("Java Basics");
            book.setAuthor("John Doe");
            book.setIsbn("ISBN13212312223");
            book.setPrice(BigDecimal.valueOf(19.99));
        });

        Book bookToSave2 = bookTemplate(book -> {
            book.setTitle("Spring Boot Guide");
            book.setAuthor("Jane Smith");
            book.setIsbn("ISBN455556");
            book.setPrice(BigDecimal.valueOf(25.49));
        });

        Book savedBook1 = bookRepository.save(bookToSave1);
        createdBookIds.add(savedBook1.getId());

        Book savedBook2 = bookRepository.save(bookToSave2);
        createdBookIds.add(savedBook2.getId());

        Response response = given()
                .auth().preemptive().basic("admin@example.com", "admin1234")
                .param("description", "123")
                .contentType(ContentType.JSON)
                .get("/api/v1/books" + "/search");

        List<BookResponse> books = response.body().as(new TypeRef<List<BookResponse>>() {
        });

        assertThat(books).hasSize(2);
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
