package com.example.society.Controller;

import com.example.society.DTO.Request.CreatePostRequest;
import com.example.society.DTO.Request.EmotionPostRequest;
import com.example.society.DTO.Response.APIResponse;
import com.example.society.DTO.Response.CommentResponse;
import com.example.society.DTO.Response.PostResponse;
import com.example.society.Service.Interface.CommentService;
import com.example.society.Service.Interface.EmotionService;
import com.example.society.Service.Interface.PostService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/post")
@CrossOrigin(origins = "*") // Cho phép mọi nguồn gửi yêu cầu
@Validated
public class PostController {
    @Autowired
    PostService postService;

    @Autowired
    EmotionService emotionService;

    @Autowired
    CommentService commentService;

    @GetMapping("/images")
    public APIResponse<List<String>> getUserPostImages(@RequestParam String userID){
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
    @PostMapping("/create")
    public APIResponse<Void> createPost(@RequestBody CreatePostRequest createPostRequest) {
        APIResponse<Void> apiResponse = new APIResponse<>();
        apiResponse.setResult(postService.createPost(createPostRequest));
        return apiResponse;
    }

    @PostMapping("/getEmotion")
    public APIResponse<String> getEmotion(@RequestBody EmotionPostRequest emotionPostRequest) {
        APIResponse<String> apiResponse = new APIResponse<>();
        apiResponse.setResult(emotionService.getEmotion(emotionPostRequest));
        return apiResponse;
    }
    @PostMapping("/getComment")
    public APIResponse<List<CommentResponse>> getComment(@RequestBody List<String> comments) {
        APIResponse<List<CommentResponse>> apiResponse = new APIResponse<>();
        List<ObjectId> commentsList = new ArrayList<>();
        // Duyệt qua từng chuỗi ID trong comments và chuyển đổi thành ObjectId
        for (String comment : comments) {
            try {
                commentsList.add(new ObjectId(comment));
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }
        apiResponse.setResult(commentService.getComment(commentsList));
        return apiResponse;
    }

    @GetMapping("/getCommentByPostID")
    public APIResponse<List<CommentResponse>> getCommentByPostID(@RequestParam String postID) {
        APIResponse<List<CommentResponse>> apiResponse = new APIResponse<>();
        ObjectId objectId = new ObjectId(postID);
        apiResponse.setResult(commentService.getCommentByPostID(objectId));
        return apiResponse;
    }
}
