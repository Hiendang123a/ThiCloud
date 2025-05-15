package com.example.society.Service.Interface;

import com.example.society.DTO.Request.CreatePostRequest;
import com.example.society.DTO.Response.PostResponse;
import org.bson.types.ObjectId;

import java.util.List;

public interface PostService {
    List<String> getUserPostImages(ObjectId userID);
    List<PostResponse> getHomeFeedPostsForUser(ObjectId userID, int page, int size);
    List<PostResponse> getHomeFeedPostsForGuest(int page, int size);
    Void createPost(CreatePostRequest createPostRequest);
}
