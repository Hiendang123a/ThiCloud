package com.example.society.Mapper;

import com.example.society.DTO.Response.PostResponse;
import com.example.society.Entity.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring",uses = ConverterMapper.class)
public interface PostMapper {
    @Mapping(source = "postID", target = "postID", qualifiedByName = "objectIDToString")
    @Mapping(source = "userID", target = "userID", qualifiedByName = "objectIDToString")
    @Mapping(source = "emotions", target = "emotions", qualifiedByName = "listObjectIDToListString")
    @Mapping(source = "comments", target = "comments", qualifiedByName = "listObjectIDToListString")
    PostResponse toPostResponse (Post post);
    List<PostResponse> toPostResponseList(List<Post> posts);
}
