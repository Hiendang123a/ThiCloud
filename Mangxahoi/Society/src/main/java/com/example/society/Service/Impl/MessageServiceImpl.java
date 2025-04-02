package com.example.society.Service.Impl;

import com.example.society.DTO.Request.CreateMessageRequest;
import com.example.society.DTO.Request.MarkSeenRequest;
import com.example.society.DTO.Request.ReadMessageRequest;
import com.example.society.DTO.Response.BubbleResponse;
import com.example.society.DTO.Response.LastMessageResponse;
import com.example.society.DTO.Response.MessageResponse;
import com.example.society.Entity.Message;
import com.example.society.Exception.AppException;
import com.example.society.Exception.ErrorCode;
import com.example.society.Mapper.MessageMapper;
import com.example.society.Repository.IMessageRepository;
import com.example.society.Service.Interface.FriendShipService;
import com.example.society.Service.Interface.MessageService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class MessageServiceImpl implements MessageService {
    @Autowired
    IMessageRepository messageRepository;

    @Autowired
    FriendShipService friendShipService;

    @Autowired
    MessageMapper messageMapper;

    @Transactional
    @Override
    public MessageResponse createMessage(CreateMessageRequest createMessageRequest) {
        Message message = messageMapper.toMessage(createMessageRequest);
        message.setSentAt(new Date());
        message.setSeenAt(null);
        message.setEditedAt(null);
        message.setDeleted(false);
        return messageMapper.toMessageResponse(messageRepository.save(message));
    }

    @Override
    public List<MessageResponse> readMessages(ReadMessageRequest readMessageRequest) {

        ObjectId senderID = new ObjectId(readMessageRequest.getSenderID());
        ObjectId receiverID = new ObjectId(readMessageRequest.getReceiverID());
        Date cursor = readMessageRequest.getCursor();
        Pageable pageable = PageRequest.of(0, readMessageRequest.getLimit(), Sort.by(Sort.Direction.DESC, "sentAt"));
        List<Message> messages;
        Date currentTimestamp = new Date();
        if (cursor == null) {
            messages = messageRepository.findMessagesBetweenUsers(senderID, receiverID, currentTimestamp, pageable);
        } else {
            messages = messageRepository.findMessagesBetweenUsers(senderID, receiverID, cursor, pageable);
        }
        List<MessageResponse> responses = new ArrayList<>();
        for (Message message : messages) {
            responses.add(messageMapper.toMessageResponse(message));
        }
        return responses;
    }

    @Transactional
    @Override
    public void markMessagesAsSeen(MarkSeenRequest markSeenRequest) {
        List<Message> unseenMessages = messageRepository.findUnseenMessages(new ObjectId(markSeenRequest.getReceiverID())
                , new ObjectId(markSeenRequest.getSenderID()));

        if (!unseenMessages.isEmpty()) {
            Date now = new Date();
            unseenMessages.forEach(message -> message.setSeenAt(now));
            messageRepository.saveAll(unseenMessages);
        }
    }

    @Transactional
    @Override
    public void softDeleteMessage(String messageID) {
        Message message = messageRepository.findById(new ObjectId(messageID))
                .orElseThrow(() -> new AppException(ErrorCode.SYSTEM_ERROR));
        message.setDeleted(true);
        messageRepository.save(message);
    }

    @Override
    public List<LastMessageResponse> lastMessage(String userID) {
        List<BubbleResponse> bubbleResponseList = friendShipService.getFriendsList(new ObjectId(userID));
        List<MessageResponse> messageResponseList = new ArrayList<>();
        List<LastMessageResponse> lastMessageResponseList = new ArrayList<>();
        Pageable pageable = PageRequest.of(0, 1, Sort.by(Sort.Direction.DESC, "sentAt"));

        // Thêm tin nhắn cho bản thân mình là null vì không hiển thị
        messageResponseList.add(null);

        for (int i = 1; i < bubbleResponseList.size(); i++) {
            BubbleResponse bubbleResponse = bubbleResponseList.get(i);
            Message message = messageRepository.findLastMessage(new ObjectId(userID), new ObjectId(bubbleResponse.getUserID()), pageable)
                    .stream()
                    .findFirst()
                    .orElse(null);
            messageResponseList.add(messageMapper.toMessageResponse(message));
        }

        // Danh sách chỉ mục để theo dõi thứ tự
        List<Integer> indexList = new ArrayList<>();
        for (int i = 1; i < bubbleResponseList.size(); i++) {
            indexList.add(i);
        }

        // Sắp xếp chỉ mục theo `sentAt`, nếu null thì đưa ra sau
        indexList.sort((i1, i2) -> {
            MessageResponse m1 = messageResponseList.get(i1);
            MessageResponse m2 = messageResponseList.get(i2);

            if (m1 == null && m2 == null) return 0;
            if (m1 == null) return 1;
            if (m2 == null) return -1;

            return m2.getSentAt().compareTo(m1.getSentAt()); // Giảm dần
        });



        lastMessageResponseList.add(new LastMessageResponse(bubbleResponseList.get(0),messageResponseList.get(0))); // Giữ nguyên phần tử đầu

        for (int index : indexList) {
            lastMessageResponseList.add(new LastMessageResponse(bubbleResponseList.get(index),messageResponseList.get(index)));
        }

        return lastMessageResponseList;
    }
}
