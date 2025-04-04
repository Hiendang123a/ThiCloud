package com.example.society.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.sql.Timestamp;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@Document(collection = "follow")
public class Follow {
    @Id
    private ObjectId id;
    private ObjectId user1;
    private ObjectId user2;
    private FollowAction status;
    private Date createdAt = new Date();
}
