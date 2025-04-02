package com.example.app01.Api;
import com.example.app01.DTO.Response.APIResponse;
import com.example.app01.DTO.Response.UserResponse;


import retrofit2.Call;
import retrofit2.http.GET;

public interface UserService {
    @GET("/api/user/profile")
    Call<APIResponse<UserResponse>> getUser();
}
