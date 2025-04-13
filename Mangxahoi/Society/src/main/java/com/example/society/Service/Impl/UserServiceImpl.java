package com.example.society.Service.Impl;

import com.example.society.DTO.Request.UpdateUserRequest;
import com.example.society.DTO.Response.BubbleResponse;
import com.example.society.DTO.Response.UserResponse;
import com.example.society.Entity.User;
import com.example.society.Exception.AppException;
import com.example.society.Exception.ErrorCode;
import com.example.society.Mapper.UserMapper;
import com.example.society.Repository.IUserRepository;
import com.example.society.Service.Interface.UserService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    IUserRepository userRepository;

    @Autowired
    UserMapper userMapper;
    @Override
    public UserResponse getUser(ObjectId userId) {
        User user = userRepository.findUserByUserID(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        return userMapper.toUserResponse(user);
    }

    @Override
    public List<BubbleResponse> searchUsers(String query, ObjectId userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        List<User> users = userRepository.findByNameContainingIgnoreCase(query, pageable).getContent();

        List<BubbleResponse> bubbleResponseList = new ArrayList<>();
        for (User user : users) {
            BubbleResponse bubbleResponse = new BubbleResponse(user.getUserID(),user.getName(),user.getAvatar());
            if(!user.getUserID().equals(userId))
                bubbleResponseList.add(bubbleResponse);
        }
        return bubbleResponseList;
    }

    @Override
    public UserResponse updateUser(ObjectId userID, UpdateUserRequest updateUserRequest) {
        User existingUser = userRepository.findById(userID)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        if (updateUserRequest.getName() != null) {
            existingUser.setName(updateUserRequest.getName());
        }
        if (updateUserRequest.getGender() != null) {
            existingUser.setGender(updateUserRequest.getGender());
        }
        if (updateUserRequest.getDob() != null) {
            existingUser.setDob(updateUserRequest.getDob());
        }
        if (updateUserRequest.getBio() != null) {
            existingUser.setBio(updateUserRequest.getBio());
        }
        if (updateUserRequest.getPhone() != null) {
            existingUser.setPhone(updateUserRequest.getPhone());
        }
        if (updateUserRequest.getAvatar() != null) {
            existingUser.setAvatar(updateUserRequest.getAvatar());
        }
        userRepository.save(existingUser);
        return userMapper.toUserResponse(existingUser);
    }
}
