package com.example.society.Entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "messages")
public class Message {
    @Id
    private ObjectId messageID;
    @Indexed
    private ObjectId senderID;  // Thêm index cho senderID

    @Indexed
    private ObjectId receiverID;  // Thêm index cho receiverID
    private String content;
    private String imageUrl;
    private Date sentAt;
    private Date seenAt;
    private Date editedAt;
    @JsonProperty("isDeleted")
    private boolean isDeleted;
}