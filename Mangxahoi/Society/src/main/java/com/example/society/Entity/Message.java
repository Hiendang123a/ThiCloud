package com.example.society.Entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.List;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "messages")
public class Message {
    @Id
    private ObjectId messageID;
    private ObjectId senderID;
    private ObjectId receiverID;
    private String content;
    private String imageUrl;
    private Date sentAt;
    private Date seenAt;
    private Date editedAt;
    @JsonProperty("isDeleted")
    private boolean isDeleted;
}