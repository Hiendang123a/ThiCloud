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

    // ✅ Lấy danh sách follow request (chỉ dành cho tài khoản private)
    @GetMapping("/requests")
    public APIResponse<List<BubbleResponse>> getFollowRequests() {
        String receiverID = SecurityContextHolder.getContext().getAuthentication().getName();
        APIResponse<List<BubbleResponse>> apiResponse = new APIResponse<>();
        apiResponse.setResult(followService.getFollowRequests(new ObjectId(receiverID)));
        return apiResponse;
    }

    // ✅ Lấy số lượng followers
    @GetMapping("/followers/count")
    public APIResponse<Integer> getFollowersCount(@RequestParam String userID) {
        APIResponse<Integer> apiResponse = new APIResponse<>();
        apiResponse.setResult(followService.getFollowersCount(new ObjectId(userID)));
        return apiResponse;
    }

    // ✅ Lấy số lượng following
    @GetMapping("/following/count")
    public APIResponse<Integer> getFollowingCount(@RequestParam String userID) {
        APIResponse<Integer> apiResponse = new APIResponse<>();
        apiResponse.setResult(followService.getFollowingCount(new ObjectId(userID)));
        return apiResponse;
    }

    //is following?
    @GetMapping("/is-following/{receiverID}")
    public APIResponse<Boolean> isFollowing(@PathVariable String receiverID) {
        String senderID = SecurityContextHolder.getContext().getAuthentication().getName();
        APIResponse<Boolean> apiResponse = new APIResponse<>();
        apiResponse.setResult(followService.isFollowing(new ObjectId(senderID), new ObjectId(receiverID)));
        return apiResponse;
    }

    //remove follower
    @DeleteMapping("/remove")
    public APIResponse<String> removeFollower(@RequestParam String receiverID) {
        // Gọi service để xóa người theo dõi
        String senderID = SecurityContextHolder.getContext().getAuthentication().getName();
        APIResponse<String> apiResponse = new APIResponse<>();
        apiResponse.setResult(followService.removeFollower(new ObjectId(senderID), new ObjectId(receiverID)));
        return apiResponse;
    }

    //
    @GetMapping("/status")
    public APIResponse<Boolean> checkFollowRequestStatus(@RequestParam String receiverID) {
        String senderID = SecurityContextHolder.getContext().getAuthentication().getName();
        APIResponse<Boolean> apiResponse = new APIResponse<>();
        apiResponse.setResult(followService.hasPendingRequest(new ObjectId(senderID), new ObjectId(receiverID)));
        return apiResponse;
    }
}
