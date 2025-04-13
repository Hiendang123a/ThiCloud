package com.example.society.Controller;

import com.example.society.DTO.Response.APIResponse;
import com.example.society.DTO.Response.PostResponse;
import com.example.society.Service.Interface.PostService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/post")
@CrossOrigin(origins = "*") // Cho phép mọi nguồn gửi yêu cầu
@Validated
public class PostController {
    @Autowired
    PostService postService;
    @GetMapping("/images")
    public APIResponse<List<String>> getUserPostImages(@RequestBody String userID){
        APIResponse<List<String>> apiResponse = new APIResponse<>();
        apiResponse.setResult(postService.getUserPostImages(new ObjectId(userID)));
        return apiResponse;
    }

    @GetMapping("/feed")
    public APIResponse<List<PostResponse>> getHomeFeedPosts(
            @RequestParam(required = false) String userID,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ){
        APIResponse<List<PostResponse>> apiResponse = new APIResponse<>();
        if(userID == null) {
            System.out.println("📢 Đang lấy bài viết cho **Guest**");
            apiResponse.setResult(postService.getHomeFeedPostsForGuest(page, size));
        } else {
            System.out.println("📢 Đang lấy bài viết cho **User**: " + userID);
            apiResponse.setResult(postService.getHomeFeedPostsForUser(new ObjectId(userID), page, size));
        }
        return apiResponse;
    }
}
