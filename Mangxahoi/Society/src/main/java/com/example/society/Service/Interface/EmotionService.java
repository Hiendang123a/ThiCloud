package com.example.society.Service.Interface;

import com.example.society.DTO.Request.EmotionCommentRequest;
import com.example.society.DTO.Request.EmotionPostRequest;
import com.example.society.DTO.Response.EmotionCommentResponse;
import com.example.society.DTO.Response.EmotionPostResponse;

public interface EmotionService {
    EmotionPostResponse emotionPost(EmotionPostRequest emotionPostRequest);
    EmotionCommentResponse emotionComment(EmotionCommentRequest emotionCommentRequest);
}
