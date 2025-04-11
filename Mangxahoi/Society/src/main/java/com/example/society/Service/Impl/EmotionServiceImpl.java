package com.example.society.Service.Impl;

import com.example.society.DTO.Request.EmotionCommentRequest;
import com.example.society.DTO.Request.EmotionPostRequest;
import com.example.society.DTO.Response.EmotionCommentResponse;
import com.example.society.DTO.Response.EmotionPostResponse;
import com.example.society.Entity.*;
import com.example.society.Exception.AppException;
import com.example.society.Exception.ErrorCode;
import com.example.society.Mapper.UserMapper;
import com.example.society.Repository.*;
import com.example.society.Service.Interface.EmotionService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;
@Service
public class EmotionServiceImpl implements EmotionService {

    @Autowired
    IPostEmotionRepository postEmotionRepository;

    @Autowired
    ICommentEmotionRepository commentEmotionRepository;

    @Autowired
    ICommentRepository commentRepository;

    @Autowired
    IPostRepository postRepository;

    @Autowired
    IUserRepository userRepository;

    @Autowired
    UserMapper userMapper;

    @Override
    public EmotionPostResponse emotionPost(EmotionPostRequest emotionPostRequest) {
        PostEmotion postEmotion = new PostEmotion();
        postEmotion.setUserID(new ObjectId(emotionPostRequest.getUserID()));
        postEmotion.setCreatedAt(new Date());

        postEmotionRepository.save(postEmotion);
        Optional<Post> post = postRepository.findByPostID(new ObjectId(emotionPostRequest.getPostID()));

        Post postEntity = post.orElseThrow(() -> new AppException(ErrorCode.POST_NOT_EXITS));

        postEntity.getEmotions().add(postEmotion.getEmotionID());
        postEntity.setEmotionsCount(postEntity.getEmotions().size());
        postRepository.save(postEntity);

        User user = userRepository.findUserByUserID(new ObjectId(emotionPostRequest.getUserID()))
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_EXITS));

        return userMapper.toEmotionPostResponse(user, postEmotion);
    }


    @Override
    public EmotionCommentResponse emotionComment(EmotionCommentRequest emotionCommentRequest) {
        CommentEmotion commentEmotion = new CommentEmotion();
        commentEmotion.setUserID(new ObjectId(emotionCommentRequest.getUserID()));
        commentEmotion.setCreatedAt(new Date());

        commentEmotionRepository.save(commentEmotion);
        Optional<PostComment> postComment = commentRepository.findByCommentID(new ObjectId(emotionCommentRequest.getCommentID()));

        PostComment postCommentEntity = postComment.orElseThrow(() -> new AppException(ErrorCode.POST_NOT_EXITS));

        postCommentEntity.getEmotions().add(commentEmotion.getEmotionID());
        postCommentEntity.setEmotionsCount(postCommentEntity.getEmotions().size());
        commentRepository.save(postCommentEntity);

        User user = userRepository.findUserByUserID(new ObjectId(emotionCommentRequest.getUserID()))
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_EXITS));

        return userMapper.toEmotionCommentResponse(user, commentEmotion);
    }
}
