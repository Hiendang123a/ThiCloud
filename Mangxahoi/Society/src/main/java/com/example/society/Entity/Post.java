package com.example.society.Entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Document(collection = "posts")
public class Post {
    @Id
    private ObjectId postID;
    private ObjectId userID;
    private String content;
    private String imageUrl;
    private List<ObjectId> emotions;
    private List<ObjectId> comments;
    private Date createdAt = new Date();
    @Field("emotionsCount")
    private int emotionsCount;
    private int commentsCount;
}
