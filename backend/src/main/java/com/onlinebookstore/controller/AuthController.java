package com.onlinebookstore.controller;

import static org.springframework.http.HttpStatus.CREATED;

import com.onlinebookstore.dto.UserRegistrationRequest;
import com.onlinebookstore.dto.UserResponse;
import com.onlinebookstore.mapper.UserMapper;
import com.onlinebookstore.model.User;
import com.onlinebookstore.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final UserService userService;
    private final UserMapper userMapper;

    @PostMapping("/registration")
    public ResponseEntity<UserResponse> register(@RequestBody UserRegistrationRequest request) {
        User user = userService.register(userMapper.toModel(request));
        UserResponse userResponse = userMapper.toDto(user);
        return ResponseEntity.status(CREATED).body(userResponse);
    }
}
