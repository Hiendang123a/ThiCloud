package com.example.society.Mapper;

import com.example.society.DTO.Request.CreateMessageRequest;
import com.example.society.DTO.Response.MessageResponse;
import com.example.society.Entity.Message;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-04-13T11:37:43+0700",
    comments = "version: 1.5.2.Final, compiler: javac, environment: Java 17.0.9 (Oracle Corporation)"
)
@Component
public class MessageMapperImpl implements MessageMapper {

    @Autowired
    private ConverterMapper converterMapper;

    @Override
    public Message toMessage(CreateMessageRequest createMessageRequest) {
        if ( createMessageRequest == null ) {
            return null;
        }

        Message message = new Message();

        message.setSenderID( converterMapper.stringToObjectID( createMessageRequest.getSenderID() ) );
        message.setReceiverID( converterMapper.stringToObjectID( createMessageRequest.getReceiverID() ) );
        message.setContent( createMessageRequest.getContent() );
        message.setImageUrl( createMessageRequest.getImageUrl() );

        return message;
    }

    @Override
    public MessageResponse toMessageResponse(Message message) {
        if ( message == null ) {
            return null;
        }

        MessageResponse messageResponse = new MessageResponse();

        messageResponse.setSenderID( converterMapper.objectIDToString( message.getSenderID() ) );
        messageResponse.setReceiverID( converterMapper.objectIDToString( message.getReceiverID() ) );
        messageResponse.setMessageID( converterMapper.objectIDToString( message.getMessageID() ) );
        messageResponse.setContent( message.getContent() );
        messageResponse.setImageUrl( message.getImageUrl() );
        messageResponse.setSentAt( message.getSentAt() );
        messageResponse.setSeenAt( message.getSeenAt() );
        messageResponse.setEditedAt( message.getEditedAt() );
        messageResponse.setDeleted( message.isDeleted() );

        return messageResponse;
    }
}
