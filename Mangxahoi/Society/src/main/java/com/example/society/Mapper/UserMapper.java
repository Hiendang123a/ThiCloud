package com.example.society.Mapper;
import com.example.society.DTO.Request.UserDTO;
import com.example.society.DTO.Response.EmotionCommentResponse;
import com.example.society.DTO.Response.EmotionPostResponse;
import com.example.society.DTO.Response.UserResponse;
import com.example.society.Entity.CommentEmotion;
import com.example.society.Entity.PostEmotion;
import com.example.society.Entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = ConverterMapper.class)
public interface UserMapper {
    @Mapping(target = "bio", expression = "java(userDTO.getBio() != null ? userDTO.getBio().trim() : null)")
    @Mapping(target = "avatar", expression = "java(userDTO.getAvatar() != null ? userDTO.getAvatar().trim() : null)")
    User toUser (UserDTO userDTO);

    @Mapping(source = "userID", target = "userID", qualifiedByName = "objectIDToString")
    UserResponse toUserResponse(User user);

    @Mapping(source = "user.userID", target = "userID", qualifiedByName = "objectIDToString")
    @Mapping(source = "user.name", target = "name")
    @Mapping(source = "user.avatar", target = "avatar")
    @Mapping(source = "emotion.createdAt", target = "createdAt")
    EmotionPostResponse toEmotionPostResponse(User user, PostEmotion emotion);

    @Mapping(source = "user.userID", target = "userID", qualifiedByName = "objectIDToString")
    @Mapping(source = "user.name", target = "name")
    @Mapping(source = "user.avatar", target = "avatar")
    @Mapping(source = "commentEmotion.createdAt", target = "createdAt")
    EmotionCommentResponse toEmotionCommentResponse(User user, CommentEmotion commentEmotion);
}


