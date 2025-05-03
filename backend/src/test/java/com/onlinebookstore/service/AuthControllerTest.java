package com.onlinebookstore.service;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.onlinebookstore.BaseTest;
import com.onlinebookstore.dto.UserRegistrationRequest;
import com.onlinebookstore.dto.UserResponse;
import com.onlinebookstore.mapper.UserMapper;
import com.onlinebookstore.model.User;
import com.onlinebookstore.repository.user.UserRepository;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import jakarta.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;

@RequiredArgsConstructor
@TestPropertySource(locations = "classpath:application-test.properties")
class AuthControllerTest extends BaseTest {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestSetup testSetup;

    @Autowired
    private TestCleanup testCleanup;

    private final List<Long> createdUserIds = new ArrayList<>();
    private final List<Long> createdRoleIds = new ArrayList<>();
    @Autowired
    private UserMapper userMapper;

    @BeforeEach
    void setup() {
        testSetup.createUsers(createdUserIds, createdRoleIds);
    }

    @AfterEach
    void clean() {
        testCleanup.cleanupUsersAfterTest(createdUserIds, createdRoleIds);
    }

    @Test
    void callRegistrationEndpointSuccess() {
        UserRegistrationRequest request = createUserRegistrationRequest(
                "newuser@mail.com", "jde23pz234W11", "newFirstName",
                "newLastName", "newAddress"
        );

        Response response = given()
                .contentType(ContentType.JSON)
                .body(request)
                .post("/api/v1/auth/registration");

        assertEquals(201, response.getStatusCode(),
                "Controller should respond with HttpStatus.CREATED");

        UserResponse userResponse = response.body().as(UserResponse.class);

        createdUserIds.add(userResponse.getId());

        User createdUser = userRepository.findById(userResponse.getId()).orElseThrow(
                () -> new EntityNotFoundException("Can't find user by id" + userResponse.getId())
        );

        assertThat(userResponse.getId()).isEqualTo(createdUser.getId());
        assertThat(userResponse).isNotNull();
        assertThat(userResponse.getFirstName()).isEqualTo(request.getFirstName());
        assertThat(userResponse.getLastName()).isEqualTo(request.getLastName());
        assertThat(userResponse.getEmail()).isEqualTo(request.getEmail());
        assertThat(userResponse.getShippingAddress()).isEqualTo(request.getShippingAddress());

    }

    @Test
    void callRegistrationEndpointFailEmailAlreadyExists() {
        User existingUser = userRepository.findByEmail("user@example.com").orElseThrow(
                () -> new EntityNotFoundException("Can't find user by email")
        );
        UserRegistrationRequest request = createUserRegistrationRequest(existingUser.getEmail(),
                "jde23pz234W11", "newFirstName", "newLastName", "newAdress");

        Response response = given()
                .contentType(ContentType.JSON)
                .body(request)
                .post("/api/v1/auth/registration");

        assertEquals(409, response.getStatusCode(),
                "Controller should respond with HttpStatus.CONFLICT");
    }

    //    When we have endpoint login, we should implement it.
    //    @Test
    //    void callLoginEndpointSuccessfully() {
    //
    //    }
    //
    //    @Test
    //    void callLoginEndpointFailWrongPassword() {
    //    }
    //
    //    @Test
    //    void callLoginEndpointFailDoesNotExist() {
    //    }
    private UserRegistrationRequest createUserRegistrationRequest(
            String email, String password, String firstName,
            String lastName, String shippingAddress
    ) {
        return new UserRegistrationRequest() {
            {
                setEmail(email);
                setPassword(password);
                setFirstName(firstName);
                setLastName(lastName);
                setShippingAddress(shippingAddress);
            }
        };
    }
}
