package com.onlinebookstore.mapper;

import com.onlinebookstore.config.MapperConfig;
import com.onlinebookstore.dto.UserRegistrationRequest;
import com.onlinebookstore.dto.UserResponse;
import com.onlinebookstore.model.User;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.security.crypto.password.PasswordEncoder;

@Mapper(config = MapperConfig.class)
public interface UserMapper {
    UserResponse toDto(User user);

    @Mapping(target = "password", expression = "java(encoder.encode(requestDto.getPassword()))")
    @Mapping(target = "email", source = "requestDto.email")
    @Mapping(target = "enabled", constant = "true")
    User toModel(UserRegistrationRequest requestDto,@Context PasswordEncoder encoder);
}
