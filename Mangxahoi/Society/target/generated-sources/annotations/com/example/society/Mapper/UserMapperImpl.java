package com.example.society.Mapper;

import com.example.society.DTO.Request.UserDTO;
import com.example.society.DTO.Response.EmotionCommentResponse;
import com.example.society.DTO.Response.EmotionPostResponse;
import com.example.society.DTO.Response.UserResponse;
import com.example.society.Entity.CommentEmotion;
import com.example.society.Entity.PostEmotion;
import com.example.society.Entity.User;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-04-11T23:37:27+0700",
    comments = "version: 1.5.2.Final, compiler: javac, environment: Java 17.0.9 (Oracle Corporation)"
)
@Component
public class UserMapperImpl implements UserMapper {

    @Autowired
    private ConverterMapper converterMapper;

    @Override
    public User toUser(UserDTO userDTO) {
        if ( userDTO == null ) {
            return null;
        }

        User user = new User();

        user.setName( userDTO.getName() );
        user.setGender( userDTO.getGender() );
        user.setDob( userDTO.getDob() );
        user.setPhone( userDTO.getPhone() );

        user.setBio( userDTO.getBio() != null ? userDTO.getBio().trim() : null );
        user.setAvatar( userDTO.getAvatar() != null ? userDTO.getAvatar().trim() : null );

        return user;
    }

    @Override
    public UserResponse toUserResponse(User user) {
        if ( user == null ) {
            return null;
        }

        UserResponse userResponse = new UserResponse();

        userResponse.setUserID( converterMapper.objectIDToString( user.getUserID() ) );
        userResponse.setName( user.getName() );
        userResponse.setGender( user.getGender() );
        userResponse.setDob( user.getDob() );
        userResponse.setBio( user.getBio() );
        userResponse.setPhone( user.getPhone() );
        userResponse.setAvatar( user.getAvatar() );

        return userResponse;
    }

    @Override
    public EmotionPostResponse toEmotionPostResponse(User user, PostEmotion emotion) {
        if ( user == null && emotion == null ) {
            return null;
        }

        EmotionPostResponse emotionPostResponse = new EmotionPostResponse();

        if ( user != null ) {
            emotionPostResponse.setUserID( converterMapper.objectIDToString( user.getUserID() ) );
            emotionPostResponse.setName( user.getName() );
            emotionPostResponse.setAvatar( user.getAvatar() );
        }
        if ( emotion != null ) {
            emotionPostResponse.setCreatedAt( emotion.getCreatedAt() );
        }

        return emotionPostResponse;
    }

    @Override
    public EmotionCommentResponse toEmotionCommentResponse(User user, CommentEmotion commentEmotion) {
        if ( user == null && commentEmotion == null ) {
            return null;
        }

        EmotionCommentResponse emotionCommentResponse = new EmotionCommentResponse();

        if ( user != null ) {
            emotionCommentResponse.setUserID( converterMapper.objectIDToString( user.getUserID() ) );
            emotionCommentResponse.setName( user.getName() );
            emotionCommentResponse.setAvatar( user.getAvatar() );
        }
        if ( commentEmotion != null ) {
            emotionCommentResponse.setCreatedAt( commentEmotion.getCreatedAt() );
        }

        return emotionCommentResponse;
    }
}
