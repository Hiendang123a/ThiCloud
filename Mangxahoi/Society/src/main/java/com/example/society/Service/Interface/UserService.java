package com.example.society.Service.Interface;

import com.example.society.DTO.Response.BubbleResponse;
import com.example.society.DTO.Response.UserResponse;
import com.example.society.Entity.User;

import java.util.List;

public interface UserService {
    UserResponse getUser (String id);

    List<BubbleResponse> searchUsers(String query, String userId);
}
