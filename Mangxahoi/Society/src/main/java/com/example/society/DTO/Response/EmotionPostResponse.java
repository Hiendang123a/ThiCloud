package com.example.society.DTO.Response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmotionPostResponse {
    String userID;
    String name;
    String avatar;
    Date createdAt;
}
