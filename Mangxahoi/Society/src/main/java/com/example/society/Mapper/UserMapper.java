package com.example.society.Mapper;
import com.example.society.DTO.Request.UserDTO;
import com.example.society.DTO.Response.UserResponse;
import com.example.society.Entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = ConverterMapper.class)
public interface UserMapper {
    User toUser (UserDTO userDTO);

    @Mapping(source = "userID", target = "userID", qualifiedByName = "objectIDToString")
    UserResponse toUserResponseDTO (User user);
}


