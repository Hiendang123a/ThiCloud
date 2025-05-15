package com.example.app01.Api;
import com.example.app01.DTO.Request.UpdateUserRequest;
import com.example.app01.DTO.Request.VerifyOTPRepassRequest;
import com.example.app01.DTO.Response.APIResponse;
import com.example.app01.DTO.Response.BubbleResponse;
import com.example.app01.DTO.Response.UserResponse;


import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface UserService {
    @GET("/api/user/is/{userID}")
    Call<APIResponse<UserResponse>> getUser(@Path("userID") String userID);

    @GET("/api/user/search")
    Call<APIResponse<List<BubbleResponse>>> searchUsers(@Query("query") String query, @Query("page") int page, @Query("size") int size);

    // API cập nhật thông tin User
    @PUT("/api/user/updateProfile")
    Call <APIResponse<UserResponse>> updateBasicInfo(@Body UpdateUserRequest updateUserRequest);
}
