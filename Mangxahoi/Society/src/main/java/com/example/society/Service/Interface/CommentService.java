package com.example.society.Service.Interface;

import com.example.society.DTO.Request.CommentRequest;
import com.example.society.DTO.Response.CommentResponse;

public interface CommentService {
    CommentResponse comment(CommentRequest commentRequest);
}
