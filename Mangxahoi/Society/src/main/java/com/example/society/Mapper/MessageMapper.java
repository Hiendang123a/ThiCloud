package com.example.society.Mapper;

import com.example.society.DTO.Request.CreateMessageRequest;
import com.example.society.DTO.Response.MessageResponse;
import com.example.society.Entity.Message;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring",uses = ConverterMapper.class)
public interface MessageMapper {
    @Mapping(source = "senderID", target = "senderID", qualifiedByName = "stringToObjectID")
    @Mapping(source = "receiverID", target = "receiverID", qualifiedByName = "stringToObjectID")
    Message toMessage(CreateMessageRequest createMessageRequest);

    @Mapping(source = "senderID", target = "senderID", qualifiedByName = "objectIDToString")
    @Mapping(source = "receiverID", target = "receiverID", qualifiedByName = "objectIDToString")
    @Mapping(source = "messageID", target = "messageID", qualifiedByName = "objectIDToString")
    MessageResponse toMessageResponse(Message message);
}
