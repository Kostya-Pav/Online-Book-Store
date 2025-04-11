package com.onlinebookstore.mapper;

import com.onlinebookstore.config.MapperConfig;
import com.onlinebookstore.dto.UserRegistrationRequest;
import com.onlinebookstore.dto.UserResponse;
import com.onlinebookstore.model.User;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface UserMapper {
    UserResponse toDto(User user);

    User toModel(UserRegistrationRequest requestDto);
}
