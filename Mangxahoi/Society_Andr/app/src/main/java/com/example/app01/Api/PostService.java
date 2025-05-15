package com.example.app01.Api;

import com.example.app01.DTO.Request.CreatePostRequest;
import com.example.app01.DTO.Request.EmotionPostRequest;
import com.example.app01.DTO.Response.APIResponse;
import com.example.app01.DTO.Response.CommentResponse;
import com.example.app01.DTO.Response.PostResponse;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface PostService {
    @GET("/api/post/feed")
    Call<APIResponse<List<PostResponse>>> getHomeFeedPosts(
            @Query("userID") String userID,
            @Query("page") int page,
            @Query("size") int size
    );
    @GET("/api/post/images")
    Call<APIResponse<List<String>>> getUserPostImages(@Query("userID") String userID);
    @POST("/api/post/create")
    Call<APIResponse<Void>> createPost(@Body CreatePostRequest createPostRequest);
    @POST("/api/post/getEmotion")
    Call<APIResponse<String>> getEmotion(@Body EmotionPostRequest emotionPostRequest);
    @POST("/api/post/getComment")
    Call<APIResponse<List<CommentResponse>>> getComment(@Body List<String> comments);
    @GET("/api/post/getCommentByPostID")
    Call<APIResponse<List<CommentResponse>>> getCommentByPostID(@Query("postID") String postID);
}
