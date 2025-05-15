package com.example.society.Mapper;

import com.example.society.DTO.Response.CommentResponse;
import com.example.society.Entity.PostComment;
import com.example.society.Entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = ConverterMapper.class)
public interface CommentMapper {
    @Mapping(source = "user.userID", target = "userID", qualifiedByName = "objectIDToString")
    @Mapping(source = "user.name", target = "name")
    @Mapping(source = "user.avatar", target = "avatar")
    @Mapping(source = "postComment.commentID", target = "commentID", qualifiedByName = "objectIDToString")
    @Mapping(source = "postComment.content", target = "content")
    @Mapping(source = "postComment.createdAt", target = "createdAt")
    @Mapping(source = "postComment.emotions", target = "emotions", qualifiedByName = "listObjectIDToListString")
    @Mapping(source = "postComment.comments", target = "comments", qualifiedByName = "listObjectIDToListString")
    @Mapping(source = "postComment.emotionsCount", target = "emotionsCount")
    @Mapping(source = "postComment.commentsCount", target = "commentsCount")
    @Mapping(target = "type", ignore = true)
    CommentResponse toCommentResponse(User user, PostComment postComment);
}
