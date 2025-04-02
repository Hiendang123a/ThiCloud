package com.example.app01.DTO.Response;
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
    private boolean isDeleted;
}
