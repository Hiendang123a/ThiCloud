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
public class CommentResponse {
    public static String COMMENT_POST = "COMMENT_POST";
    public static String COMMENT_REPLY = "COMMENT_REPLY";
    private String commentID;
    private String userID;
    private String content;
    private Date createdAt;
    private List<String> emotions;
    private List<String> comments;
    private int emotionsCount;
    private int commentsCount;
    private String name;
    private String avatar;
    private String type;
}
