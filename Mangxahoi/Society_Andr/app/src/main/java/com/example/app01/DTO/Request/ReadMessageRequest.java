package com.example.app01.DTO.Request;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReadMessageRequest {
    private String senderID;
    private String receiverID;
    private String cursor;
    private int limit;
}