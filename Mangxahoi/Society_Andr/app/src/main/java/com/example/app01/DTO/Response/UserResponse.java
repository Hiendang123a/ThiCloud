package com.example.app01.DTO.Response;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private String userID;
    private String name;
    private String gender;
    private Date dob;
    private String address;
    private String hometown;
    private String avatar;
}

