package com.onlinebookstore.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class UserLoginRequest {
    @NotNull
    @Email
    private String email;
    @NotNull
    @Length(min = 8, max = 20)
    private String password;
}
