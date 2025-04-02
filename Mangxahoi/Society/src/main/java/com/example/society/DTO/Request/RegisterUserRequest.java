package com.example.society.DTO.Request;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegisterUserRequest {
    @Valid
    private UserDTO user;
    @Valid
    private AccountDTO account;
}