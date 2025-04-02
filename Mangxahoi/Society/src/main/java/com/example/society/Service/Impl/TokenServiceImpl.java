package com.example.society.Service.Impl;

import com.example.society.DTO.Request.RefreshTokenRequest;
import com.example.society.DTO.Response.AuthResponse;
import com.example.society.Exception.AppException;
import com.example.society.Exception.ErrorCode;
import com.example.society.Security.TokenProvider;
import com.example.society.Service.Interface.TokenService;
import org.springframework.stereotype.Service;

@Service
public class TokenServiceImpl implements TokenService {
    @Override
    public AuthResponse refreshToken(RefreshTokenRequest refreshTokenRequest) {
        TokenProvider tokenProvider = new TokenProvider();
        String refreshToKen = refreshTokenRequest.getRefreshToken();
        if(tokenProvider.validateToken(refreshToKen))
        {
            String userID = tokenProvider.getUserIDFromToken(refreshToKen);
            return new AuthResponse(tokenProvider.generateAccessToken(userID),tokenProvider.generateRefreshToken(userID),userID);
        }else
        {
            throw new AppException(ErrorCode.INVALID_REFRESH_TOKEN);
        }
    }
}
