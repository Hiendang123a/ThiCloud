package com.example.society.Entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;
import java.sql.Timestamp;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "follow")
public class Follow {
    private ObjectId id;
    private ObjectId senderUsername;
    private ObjectId receiverUsername;
    private FollowAction action;
    private Timestamp createdAt = new Timestamp(System.currentTimeMillis());
}
