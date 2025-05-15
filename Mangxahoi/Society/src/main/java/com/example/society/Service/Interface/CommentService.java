package com.example.society.Service.Interface;

import com.example.society.DTO.Request.CommentRequest;
import com.example.society.DTO.Response.CommentResponse;
import org.bson.types.ObjectId;

import java.util.List;

public interface CommentService {
    CommentResponse comment(CommentRequest commentRequest);
    List<CommentResponse> getComment(List<ObjectId> commentList);
    List<CommentResponse> getCommentByPostID(ObjectId postID);
}
