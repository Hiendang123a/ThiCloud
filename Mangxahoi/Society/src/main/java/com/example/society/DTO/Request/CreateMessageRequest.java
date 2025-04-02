package com.example.society.DTO.Request;
import com.example.society.Exception.AppException;
import com.example.society.Exception.ErrorCode;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
@Getter
@Setter
@NoArgsConstructor
public class CreateMessageRequest {
    private String senderID;
    private String receiverID;
    private String content;
    private String imageUrl;
    public CreateMessageRequest(String senderID, String receiverID, String content, String imageUrl) {
        if ((content == null && imageUrl == null) || (content != null && imageUrl != null)) {
            throw new AppException(ErrorCode.MESSAGE_NULL);
        }
        this.senderID = senderID;
        this.receiverID = receiverID;
        this.content = content;
        this.imageUrl = imageUrl;
    }
}