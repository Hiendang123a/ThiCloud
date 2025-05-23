package com.example.society.Service.Impl;

import com.example.society.DTO.Request.CommentRequest;
import com.example.society.DTO.Response.CommentResponse;
import com.example.society.Entity.Post;
import com.example.society.Entity.PostComment;
import com.example.society.Entity.User;
import com.example.society.Exception.AppException;
import com.example.society.Exception.ErrorCode;
import com.example.society.Mapper.CommentMapper;
import com.example.society.Repository.*;
import com.example.society.Service.Interface.CommentService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CommentServiceImpl implements CommentService {
    @Autowired
    ICommentRepository commentRepository;

    @Autowired
    IPostRepository postRepository;

    @Autowired
    IUserRepository userRepository;

    @Autowired
    CommentMapper commentMapper;

    @Override
    public CommentResponse comment(CommentRequest commentRequest) {
        PostComment postComment = new PostComment();
        postComment.setContent(commentRequest.getContent());
        postComment.setUserID(new ObjectId(commentRequest.getUserID()));
        postComment.setCreatedAt(new Date());
        String typeComment;
        commentRepository.save(postComment);
        if(commentRequest.getCommentID()==null || commentRequest.getCommentID().isEmpty()){
            Optional<Post> post = postRepository.findByPostID(new ObjectId(commentRequest.getPostID()));
            Post postEntity = post.orElseThrow(() -> new AppException(ErrorCode.POST_NOT_EXITS));

            postEntity.getComments().add(postComment.getCommentID());
            postEntity.setCommentsCount(postEntity.getComments().size());
            postRepository.save(postEntity);

            typeComment = CommentResponse.COMMENT_POST;
        }
        //Gửi comment vào phần Comment(Tức là nó là comment con)
        else
        {
            Optional<PostComment> postComment1 = commentRepository.findByCommentID(new ObjectId(commentRequest.getCommentID()));
            PostComment postCommentEntity = postComment1.orElseThrow(() -> new AppException(ErrorCode.POST_NOT_EXITS));

            postCommentEntity.getComments().add(postComment.getCommentID());
            postCommentEntity.setEmotionsCount(postCommentEntity.getComments().size());
            commentRepository.save(postCommentEntity);
            typeComment = CommentResponse.COMMENT_REPLY;
        }
        User user = userRepository.findUserByUserID(new ObjectId(commentRequest.getUserID()))
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_EXITS));
        CommentResponse commentResponse = commentMapper.toCommentResponse(user,postComment);
        commentResponse.setType(typeComment);
        return commentResponse;
    }

    @Override
    public List<CommentResponse> getComment(List<ObjectId> commentList) {
        List<PostComment> comments = commentRepository.findAllById(commentList);
        List<CommentResponse> commentResponses = comments.stream()
                .map(comment -> {
                    User user = userRepository.findById(comment.getUserID()).orElse(null);
                    return commentMapper.toCommentResponse(user, comment);
                })
                .collect(Collectors.toList());
        return commentResponses;
    }

    @Override
    public List<CommentResponse> getCommentByPostID(ObjectId postID) {
        Optional<Post> post = postRepository.findByPostID(postID);
        post.orElseThrow(() -> new AppException(ErrorCode.POST_NOT_EXITS));
        List<ObjectId> comments = post.get().getComments();
        List<CommentResponse> commentResponses = getComment(comments);
        System.out.println(commentResponses);
        return commentResponses;
    }
}
