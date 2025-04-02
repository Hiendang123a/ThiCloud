package com.example.society.DTO.Request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {
    @Email(message = "INVALID_USERNAME")
    @NotBlank(message = "USERNAME_FIELD")
    private String username;
    @NotBlank(message = "PASSWORD_FIELD")
    private String password;
}
