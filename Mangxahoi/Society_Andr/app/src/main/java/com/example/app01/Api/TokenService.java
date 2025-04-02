package com.example.app01.Api;

import com.example.app01.DTO.Request.RefreshTokenRequest;
import com.example.app01.DTO.Response.APIResponse;
import com.example.app01.DTO.Response.AuthResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface TokenService {
    @POST("/api/token/refresh")
    Call<APIResponse<AuthResponse>> refreshToken(@Body RefreshTokenRequest refreshTokenRequest);
}
