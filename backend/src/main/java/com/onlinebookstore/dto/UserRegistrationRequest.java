package com.onlinebookstore.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class UserRegistrationRequest {
    @NotNull
    @Email
    private String email;
    @NotNull
    @Length(min = 8, max = 20)
    private String password;
    @NotNull
    @Length(min = 8, max = 20)
    private String repeatPassword;
    @NotNull
    private String firstName;
    @NotNull
    private String lastName;
    private String shippingAddress;
}
