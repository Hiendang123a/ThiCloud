package com.example.society.Service.Interface;

import com.example.society.DTO.Request.*;
import com.example.society.DTO.Response.AuthResponse;
import com.example.society.DTO.Response.OTPResponse;

public interface AccountService {

    OTPResponse createAccount(RegisterUserRequest request);
    void verifyOTP(VerifyOTPRequest verifyOTPRequest);
    OTPResponse forgotPassword(String username);
    void verifyOTPRepass(VerifyOTPRepassRequest verifyOTPRepassRequest);
    AuthResponse login(LoginRequest loginRequest);
}