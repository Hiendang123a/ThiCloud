package com.example.society.Controller;

import com.example.society.DTO.Request.*;
import com.example.society.DTO.Response.APIResponse;
import com.example.society.DTO.Response.AuthResponse;
import com.example.society.DTO.Response.OTPResponse;
import com.example.society.Service.Interface.AccountService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/account")
@CrossOrigin(origins = "*") // Cho phép mọi nguồn gửi yêu cầu
@Validated
public class AccountController {

    @Autowired
    private AccountService accountService;
    @PostMapping("/register")
    public APIResponse<OTPResponse> register(@Valid @RequestBody RegisterUserRequest registerUserRequest) {
        APIResponse<OTPResponse> apiResponse = new APIResponse<>();
        apiResponse.setResult(accountService.createAccount(registerUserRequest));
        return apiResponse;
    }
    @PostMapping("/verify-email")
    public APIResponse<Void> verifyEmail(@Valid @RequestBody VerifyOTPRequest verifyOTPRequest) {
        APIResponse<Void> apiResponse = new APIResponse<>();
        accountService.verifyOTP(verifyOTPRequest);
        return apiResponse;
    }

    @PostMapping("/forgot-pass")
    public APIResponse<OTPResponse> forgotPass(@Valid @RequestBody Map<String, String> request ) {
        APIResponse<OTPResponse> apiResponse = new APIResponse<>();
        apiResponse.setResult(accountService.forgotPassword(request.get("username")));
        return apiResponse;
    }

    @PutMapping("/verify-email-repass")
    public APIResponse<Void> verifyEmailRepass(@Valid @RequestBody VerifyOTPRepassRequest verifyOTPRepassRequest) {
        APIResponse<Void> apiResponse = new APIResponse<>();
        accountService.verifyOTPRepass(verifyOTPRepassRequest);
        return apiResponse;
    }
    @PostMapping("/login")
    public APIResponse<AuthResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        APIResponse<AuthResponse> apiResponse = new APIResponse<>();
        apiResponse.setResult(accountService.login(loginRequest));
        return apiResponse;
    }
}