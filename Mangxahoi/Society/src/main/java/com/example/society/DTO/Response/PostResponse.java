package com.example.society.DTO.Response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostResponse {
    private String postID;
    private String userID;
    private String content;
    private String imageUrl;
    private List<String> emotions;
    private List<String> comments;
    private Date createdAt;
    private int emotionsCount;
    private int commentsCount;

}
