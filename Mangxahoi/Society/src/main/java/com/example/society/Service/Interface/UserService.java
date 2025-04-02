package com.example.society.Service.Interface;

import com.example.society.DTO.Response.UserResponse;

import java.util.List;

public interface UserService {
    UserResponse getUser (String id);
    //List<FollowInfo> searchUsers(String query, int page, int size);
}
