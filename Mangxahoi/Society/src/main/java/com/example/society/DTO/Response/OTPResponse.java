package com.example.society.DTO.Response;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OTPResponse {
    private String username;
    private String otp;
    private String message;
}