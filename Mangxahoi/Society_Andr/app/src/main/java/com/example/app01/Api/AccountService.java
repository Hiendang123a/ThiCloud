package com.example.app01.Api;

import com.example.app01.DTO.Request.LoginRequest;
import com.example.app01.DTO.Request.RegisterUserRequest;
import com.example.app01.DTO.Request.VerifyOTPRepassRequest;
import com.example.app01.DTO.Request.VerifyOTPRequest;
import com.example.app01.DTO.Response.APIResponse;
import com.example.app01.DTO.Response.AuthResponse;
import com.example.app01.DTO.Response.OTPResponse;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.PUT;

public interface AccountService {
    @POST("/api/account/login")
    Call<APIResponse<AuthResponse>> login(@Body LoginRequest loginRequest);
    @POST("/api/account/register")
    Call<APIResponse<OTPResponse>> register(@Body RegisterUserRequest registerUserRequest);
    @POST("/api/account/verify-email")
    Call<APIResponse<Void>> verifyEmail(@Body VerifyOTPRequest verifyOTPRequest);
    @POST("/api/account/forgot-pass")
    Call<APIResponse<OTPResponse>> forgetPass(@Body Map<String, String> request);
    @PUT("/api/account/verify-email-repass")
    Call<APIResponse<Void>> verifyEmailRepass(@Body VerifyOTPRepassRequest verifyOTPRepassRequest);
}
