package com.example.society.Service.Interface;

import com.example.society.DTO.Request.RefreshTokenRequest;
import com.example.society.DTO.Response.AuthResponse;

public interface TokenService {
    AuthResponse refreshToken(RefreshTokenRequest refreshTokenRequest);
}
