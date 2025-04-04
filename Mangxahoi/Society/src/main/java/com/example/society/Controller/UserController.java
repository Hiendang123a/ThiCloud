package com.example.society.Controller;

import com.example.society.DTO.Response.APIResponse;
import com.example.society.DTO.Response.BubbleResponse;
import com.example.society.DTO.Response.UserResponse;
import com.example.society.Service.Interface.UserService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "*") // Cho phép mọi nguồn gửi yêu cầu
@Validated
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/profile")
    public APIResponse<UserResponse> getUser() {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        APIResponse<UserResponse> apiResponse = new APIResponse<>();
        apiResponse.setResult(userService.getUser(userId));
        return apiResponse;
    }

    @GetMapping("/search")
    public APIResponse<List<BubbleResponse>> searchUsers(@RequestParam String query) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        APIResponse<List<BubbleResponse>> apiResponse = new APIResponse<>();
        apiResponse.setResult(userService.searchUsers(query,userId));
        return apiResponse;
    }
}
