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
        // Lấy thông tin bài viết dựa trên postID
        Optional<Post> post = postRepository.findByPostID(new ObjectId(emotionPostRequest.getPostID()));
        Post postEntity = post.orElseThrow(() -> new AppException(ErrorCode.POST_NOT_EXITS));

        ObjectId postID = new ObjectId(emotionPostRequest.getPostID());
        ObjectId userID = new ObjectId(emotionPostRequest.getUserID());

        // Lấy thông tin userEmotion để xử lý like/unlike
        Optional<PostEmotion> existingEmotion = postEmotionRepository.findByPostIDAndUserID(postID, userID);
        boolean isLiked = existingEmotion.isPresent();

        if ("like".equals(emotionPostRequest.getActionType()) && !isLiked) {
            // Nếu là "like" và chưa like, thêm emotion
            PostEmotion postEmotion = new PostEmotion();
            postEmotion.setPostID(postID); // ✅ BỔ SUNG DÒNG NÀY
            postEmotion.setUserID(userID);
            postEmotion.setCreatedAt(new Date());
            postEmotionRepository.save(postEmotion);
            postEntity.getEmotions().add(postEmotion.getEmotionID());
        } else if ("unlike".equals(emotionPostRequest.getActionType()) && isLiked) {
            // Nếu là "unlike" và đã like
            PostEmotion postEmotion = existingEmotion.get();
            postEntity.getEmotions().remove(postEmotion.getEmotionID());
            postEmotionRepository.delete(postEmotion);
        }

        // Cập nhật lại số lượng emotions
        postEntity.setEmotionsCount(postEntity.getEmotions().size());
        postRepository.save(postEntity);

        // Trả về thông tin EmotionPostResponse
        User user = userRepository.findUserByUserID(userID)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        return userMapper.toEmotionPostResponse(user, existingEmotion.orElse(null));
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

    @Override
    public String getEmotion(EmotionPostRequest emotionPostRequest) {
        ObjectId userID = new ObjectId(emotionPostRequest.getUserID());
        ObjectId postID = new ObjectId(emotionPostRequest.getPostID());
        Optional<PostEmotion> postEmotion = postEmotionRepository.findByPostIDAndUserID(postID,userID);
        postEmotion.orElseThrow(() -> new AppException(ErrorCode.POST_NOT_EXITS));
        return postEmotion.get().getEmotionID().toString();
    }
}
