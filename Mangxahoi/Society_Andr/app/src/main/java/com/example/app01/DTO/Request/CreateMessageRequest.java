package com.example.app01.DTO.Request;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateMessageRequest {
    private String senderID;
    private String receiverID;
    private String content;
    private String imageUrl;
}