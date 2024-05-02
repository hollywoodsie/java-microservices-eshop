package com.eshop.userservice.dto;

import lombok.Builder;
import lombok.Data;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data
@Builder
public class AuthRequest {

    @NotBlank(message = "Username field can not be empty")
    @Size(min = 4, max = 15, message = "Username must be between 4 and 15 characters")
    private String username;

    @NotBlank(message = "Password field can not be empty")
    @Size(max = 100)
    private String password;

    @NotBlank
    private String roles;
}