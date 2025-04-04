package com.example.society.Mapper;

import com.example.society.DTO.Request.UserDTO;
import com.example.society.DTO.Response.UserResponse;
import com.example.society.Entity.User;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-04-03T09:47:16+0700",
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
    public UserResponse toUserResponseDTO(User user) {
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
}
