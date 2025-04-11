package com.example.society.Service.Interface;

import com.example.society.DTO.Request.CreateMessageRequest;
import com.example.society.DTO.Request.ReadMessageRequest;
import com.example.society.DTO.Request.MarkSeenRequest;
import com.example.society.DTO.Response.LastMessageResponse;
import com.example.society.DTO.Response.MessageResponse;
import org.bson.types.ObjectId;

import java.util.List;

public interface MessageService {
        MessageResponse createMessage(CreateMessageRequest createMessageRequest);

        List<MessageResponse> readMessages(ReadMessageRequest readMessageRequest);

        void markMessagesAsSeen(MarkSeenRequest markSeenRequest);

        void softDeleteMessage(ObjectId messageID);
        List<LastMessageResponse> lastMessage(ObjectId userID);
}
