package com.example.society.Controller;

import com.example.society.DTO.Request.RepassRequest;
import com.example.society.DTO.Request.UpdateAccountRequest;
import com.example.society.DTO.Request.UpdateUserRequest;
import com.example.society.DTO.Response.APIResponse;
import com.example.society.DTO.Response.BubbleResponse;
import com.example.society.DTO.Response.UpdateAccountResponse;
import com.example.society.DTO.Response.UserResponse;
import com.example.society.Service.Interface.AccountService;
import com.example.society.Service.Interface.UserService;
import jakarta.validation.Valid;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private AccountService accountService;

    @GetMapping("/is/{userID}")
    public APIResponse<UserResponse> getUser(@PathVariable String userID) {
        APIResponse<UserResponse> apiResponse = new APIResponse<>();
        apiResponse.setResult(userService.getUser(new ObjectId(userID)));
        return apiResponse;
    }

    @GetMapping("/search")
    public APIResponse<List<BubbleResponse>> searchUsers(@RequestParam String query,
                                                         @RequestParam(defaultValue = "0") int page,
                                                         @RequestParam(defaultValue = "10") int size) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        APIResponse<List<BubbleResponse>> apiResponse = new APIResponse<>();
        apiResponse.setResult(userService.searchUsers(query,new ObjectId(userId),page,size));
        return apiResponse;
    }

    // API cập nhật thông tin User
    @PutMapping("/updateProfile")
    public APIResponse<UserResponse> updateBasicInfo(@Valid @RequestBody UpdateUserRequest updateUserRequest) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        APIResponse<UserResponse> apiResponse = new APIResponse<>();
        apiResponse.setResult(userService.updateUser(new ObjectId(userId),updateUserRequest));
        return apiResponse;
    }

    // API cập nhật thông tin Account đặt trong user vì nó cần Token
    @PutMapping("/updateAccount")
    public APIResponse<UpdateAccountResponse> updateAccount(@Valid @RequestBody UpdateAccountRequest updateAccountRequest) {
        String userID = SecurityContextHolder.getContext().getAuthentication().getName();
        APIResponse<UpdateAccountResponse> apiResponse = new APIResponse<>();
        apiResponse.setResult(accountService.updateAccount(new ObjectId(userID),updateAccountRequest));
        return apiResponse;
    }

    @PutMapping("/updatePassword")
    public APIResponse<Void> updatePassword(@Valid @RequestBody RepassRequest repassRequest) {
        String userID = SecurityContextHolder.getContext().getAuthentication().getName();
        APIResponse<Void> apiResponse = new APIResponse<>();
        accountService.updatePassword(new ObjectId(userID),repassRequest);
        return apiResponse;
    }
}
