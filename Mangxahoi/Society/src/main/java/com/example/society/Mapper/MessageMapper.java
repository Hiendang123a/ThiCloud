package com.example.society.Mapper;

import com.example.society.DTO.Request.CreateMessageRequest;
import com.example.society.DTO.Response.MessageResponse;
import com.example.society.Entity.Message;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring",uses = ConverterMapper.class)
public interface MessageMapper {
    @Mapping(source = "senderID", target = "senderID", qualifiedByName = "stringToObjectID")
    @Mapping(source = "receiverID", target = "receiverID", qualifiedByName = "stringToObjectID")
    @Mapping(target = "messageID", ignore = true)
    @Mapping(target = "sentAt", ignore = true)
    @Mapping(target = "seenAt", ignore = true)
    @Mapping(target = "editedAt", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    Message toMessage(CreateMessageRequest createMessageRequest);

    @Mapping(source = "senderID", target = "senderID", qualifiedByName = "objectIDToString")
    @Mapping(source = "receiverID", target = "receiverID", qualifiedByName = "objectIDToString")
    @Mapping(source = "messageID", target = "messageID", qualifiedByName = "objectIDToString")
    MessageResponse toMessageResponse(Message message);
}
