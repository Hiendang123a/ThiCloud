package com.example.society.DTO.Response;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MessageResponse {
    private String messageID;
    private String senderID;
    private String receiverID;
    private String content;
    private String imageUrl;
    private Date sentAt;
    private Date seenAt;
    private Date editedAt;
    @JsonProperty("isDeleted")
    private boolean isDeleted;
}
