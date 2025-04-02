package com.example.society.Controller;

import com.example.society.DTO.Request.RefreshTokenRequest;
import com.example.society.DTO.Response.APIResponse;
import com.example.society.DTO.Response.AuthResponse;
import com.example.society.Service.Interface.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/token")
@CrossOrigin(origins = "*") // Cho phép mọi nguồn gửi yêu cầu
public class TokenController {
    @Autowired
    private TokenService tokenService;

    @PostMapping("/refresh")
    public APIResponse<AuthResponse> refreshToken(@RequestBody RefreshTokenRequest refreshTokenRequest){
        APIResponse<AuthResponse> apiResponse = new APIResponse<>();
        apiResponse.setResult(tokenService.refreshToken(refreshTokenRequest));
        return apiResponse;
    }
}
