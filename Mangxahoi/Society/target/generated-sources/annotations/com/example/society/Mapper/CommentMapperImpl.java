package com.example.society.Mapper;

import com.example.society.DTO.Response.CommentResponse;
import com.example.society.Entity.PostComment;
import com.example.society.Entity.User;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-04-13T11:37:43+0700",
    comments = "version: 1.5.2.Final, compiler: javac, environment: Java 17.0.9 (Oracle Corporation)"
)
@Component
public class CommentMapperImpl implements CommentMapper {

    @Autowired
    private ConverterMapper converterMapper;

    @Override
    public CommentResponse toCommentResponse(User user, PostComment postComment) {
        if ( user == null && postComment == null ) {
            return null;
        }

        CommentResponse commentResponse = new CommentResponse();

        if ( user != null ) {
            commentResponse.setUserID( converterMapper.objectIDToString( user.getUserID() ) );
            commentResponse.setName( user.getName() );
            commentResponse.setAvatar( user.getAvatar() );
        }
        if ( postComment != null ) {
            commentResponse.setCommentID( converterMapper.objectIDToString( postComment.getCommentID() ) );
            commentResponse.setContent( postComment.getContent() );
            commentResponse.setCreatedAt( postComment.getCreatedAt() );
            commentResponse.setEmotions( converterMapper.listObjectIDToListString( postComment.getEmotions() ) );
            commentResponse.setComments( converterMapper.listObjectIDToListString( postComment.getComments() ) );
            commentResponse.setEmotionsCount( postComment.getEmotionsCount() );
            commentResponse.setCommentsCount( postComment.getCommentsCount() );
        }

        return commentResponse;
    }
}
