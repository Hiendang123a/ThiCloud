package com.example.society.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "comment_emotions")
public class CommentEmotion {
    @Id
    private ObjectId emotionID;
    private ObjectId commentID;
    private ObjectId userID;
    private Date createdAt;
}
