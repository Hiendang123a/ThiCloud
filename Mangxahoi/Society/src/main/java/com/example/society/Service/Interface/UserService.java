package com.example.society.Service.Interface;

import com.example.society.DTO.Response.BubbleResponse;
import com.example.society.DTO.Response.UserResponse;
import com.example.society.Entity.User;
import org.bson.types.ObjectId;

import java.util.List;

public interface UserService {
    UserResponse getUser (ObjectId id);

    List<BubbleResponse> searchUsers(String query, ObjectId userId, int page, int size);
}
