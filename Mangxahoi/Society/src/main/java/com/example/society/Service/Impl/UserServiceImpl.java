package com.example.society.Service.Impl;

import com.example.society.DTO.Response.UserResponse;
import com.example.society.Entity.Account;
import com.example.society.Entity.FollowInfo;
import com.example.society.Entity.User;
import com.example.society.Exception.AppException;
import com.example.society.Exception.ErrorCode;
import com.example.society.Mapper.UserMapper;
import com.example.society.Repository.IAccountRepository;
import com.example.society.Repository.IUserRepository;
import com.example.society.Service.Interface.UserService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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
    IAccountRepository accountRepository;
    @Autowired
    UserMapper userMapper;
    @Override
    public UserResponse getUser(String userId) {
        User user = userRepository.findUserByUserID(new ObjectId(userId))
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        return userMapper.toUserResponseDTO(user);
    }


    public List<FollowInfo> searchUsers(String query, int page, int size) {
        if (page > 4) {
            page = 4; // Giới hạn tối đa 5 trang
        }
        Pageable pageable = PageRequest.of(page, size);

        // 1. Lấy danh sách follow từ username
        Page<FollowInfo> accountPage = accountRepository.findFollowInfoByUsernameRegex(query, pageable);

        // 2. Lấy FollowInfo từ Profile theo danh sách username
        return accountPage.getContent();
    }
}
