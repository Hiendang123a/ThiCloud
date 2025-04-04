package com.example.society.Controller;

import com.example.society.DTO.Response.APIResponse;
import com.example.society.DTO.Response.BubbleResponse;
import com.example.society.DTO.Response.FollowResponse;
import com.example.society.Service.Interface.FollowService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/follow")
@CrossOrigin(origins = "*") // Cho phép mọi nguồn gửi yêu cầu
public class FollowController {
    @Autowired
    private FollowService followService;

    // ✅ Gửi follow request (hoặc tự động follow nếu account public)
    @PostMapping("/request")
    public APIResponse<FollowResponse> sendFollowRequest(@RequestParam String receiverID) {
        String senderID = SecurityContextHolder.getContext().getAuthentication().getName();
        APIResponse<FollowResponse> apiResponse = new APIResponse<>();
        apiResponse.setResult(followService.sendFollowRequest(new ObjectId(senderID), new ObjectId(receiverID)));
        return apiResponse;
    }

    // ✅ Chấp nhận follow request (chỉ dành cho tài khoản private)
    @PostMapping("/accept")
    public APIResponse<FollowResponse> acceptFollowRequest(@RequestParam String senderID) {
        String receiverID = SecurityContextHolder.getContext().getAuthentication().getName();
        APIResponse<FollowResponse> apiResponse = new APIResponse<>();
        apiResponse.setResult(followService.acceptFollowRequest(new ObjectId(senderID), new ObjectId(receiverID)));
        return apiResponse;
    }

    // ✅ Chấp nhận follow request (chỉ dành cho tài khoản private)
    @DeleteMapping("/reject")
    public APIResponse<FollowResponse> rejectFollowRequest(@RequestParam String senderID) {
        String receiverID = SecurityContextHolder.getContext().getAuthentication().getName();
        APIResponse<FollowResponse> apiResponse = new APIResponse<>();
        apiResponse.setResult(followService.rejectFollowRequest(new ObjectId(senderID), new ObjectId(receiverID)));
        return apiResponse;
    }


    // ✅ Hủy follow
    @DeleteMapping("/unfollow")
    public APIResponse<FollowResponse> unfollow(@RequestParam String receiverID) {
        String senderID = SecurityContextHolder.getContext().getAuthentication().getName();
        APIResponse<FollowResponse> apiResponse = new APIResponse<>();
        apiResponse.setResult(followService.unfollow(new ObjectId(senderID), new ObjectId(receiverID)));
        return apiResponse;
    }

    // ✅ Hủy follow request (trước khi được chấp nhận)
    @DeleteMapping("/cancel-request")
    public  APIResponse<FollowResponse> cancelFollowRequest(@RequestParam String receiverID) {
        String senderID = SecurityContextHolder.getContext().getAuthentication().getName();
        APIResponse<FollowResponse> apiResponse = new APIResponse<>();
        apiResponse.setResult(followService.cancelFollowRequest(new ObjectId(senderID), new ObjectId(receiverID)));
        return apiResponse;
    }

    // ✅ Lấy danh sách những người user đang follow (đã ACCEPT)
    @GetMapping("/following")
    public APIResponse<List<BubbleResponse>> getFollowing() {
        String senderID = SecurityContextHolder.getContext().getAuthentication().getName();
        APIResponse<List<BubbleResponse>> apiResponse = new APIResponse<>();
        apiResponse.setResult(followService.getFollowing(new ObjectId(senderID)));
        return apiResponse;
    }

    // ✅ Lấy danh sách những người follow user (đã ACCEPT)
    @GetMapping("/followers")
    public APIResponse<List<BubbleResponse>> getFollowers() {
        String receiverID = SecurityContextHolder.getContext().getAuthentication().getName();
        APIResponse<List<BubbleResponse>> apiResponse = new APIResponse<>();
        apiResponse.setResult(followService.getFollowers(new ObjectId(receiverID)));
        return apiResponse;
    }
}
