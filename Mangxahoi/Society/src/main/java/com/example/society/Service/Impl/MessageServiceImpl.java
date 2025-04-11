package com.example.society.Service.Impl;

import com.example.society.DTO.Request.CreateMessageRequest;
import com.example.society.DTO.Request.MarkSeenRequest;
import com.example.society.DTO.Request.ReadMessageRequest;
import com.example.society.DTO.Response.BubbleResponse;
import com.example.society.DTO.Response.LastMessageResponse;
import com.example.society.DTO.Response.MessageResponse;
import com.example.society.Entity.Follow;
import com.example.society.Entity.FollowAction;
import com.example.society.Entity.Message;
import com.example.society.Exception.AppException;
import com.example.society.Exception.ErrorCode;
import com.example.society.Mapper.MessageMapper;
import com.example.society.Repository.IFollowRepository;
import com.example.society.Repository.IMessageRepository;
import com.example.society.Repository.IUserRepository;
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
    private IFollowRepository followRepository;

    @Autowired
    IUserRepository userRepository;

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
        messages = messageRepository.findMessagesBetweenUsers(senderID, receiverID, Objects.requireNonNullElse(cursor, currentTimestamp), pageable);
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
    public void softDeleteMessage(ObjectId messageID) {
        Message message = messageRepository.findById(messageID)
                .orElseThrow(() -> new AppException(ErrorCode.SYSTEM_ERROR));
        message.setDeleted(true);
        messageRepository.save(message);
    }

    @Override
    public List<LastMessageResponse> lastMessage(ObjectId userID) {
        List<LastMessageResponse> lastMessageResponseList = new ArrayList<>();

        // Thêm bản thân người dùng vào đầu danh sách (chỉ có thông tin bản thân, không có tin nhắn)
        userRepository.findUserByUserID(userID).ifPresent(user -> {
            BubbleResponse bubbleResponse = new BubbleResponse(user.getUserID(), user.getName(), user.getAvatar());
            lastMessageResponseList.add(new LastMessageResponse(bubbleResponse, null, null));
        });

        // Lấy danh sách tin nhắn gần nhất
        List<Message> lastMessages = messageRepository.findLastMessages(userID);

        for (Message message : lastMessages) {
            // Xác định người còn lại trong tin nhắn (người gửi hoặc người nhận)
            ObjectId otherUserId = message.getSenderID().equals(userID) ? message.getReceiverID() : message.getSenderID();

            // Follow chéo
            boolean isFollowed = isMutualFollow(message.getSenderID(), message.getReceiverID());

            // Lọc tin nhắn nhận được và kiểm tra điều kiện theo dõi
            if (message.getReceiverID().equals(userID)) {
                if (isFollowed) {
                    // Thêm tin nhắn vào danh sách nếu người gửi đã follow mình
                    userRepository.findUserByUserID(otherUserId).ifPresent(user -> {
                        BubbleResponse bubbleResponse = new BubbleResponse(user.getUserID(), user.getName(), user.getAvatar());
                        MessageResponse messageResponse = messageMapper.toMessageResponse(message);
                        lastMessageResponseList.add(new LastMessageResponse(bubbleResponse, messageResponse, "INBOX"));
                    });
                }
            } else {
                // Tin nhắn gửi đi
                String status = isFollowed ? "INBOX" : "PENDING";
                userRepository.findUserByUserID(otherUserId).ifPresent(user -> {
                    BubbleResponse bubbleResponse = new BubbleResponse(user.getUserID(), user.getName(), user.getAvatar());
                    MessageResponse messageResponse = messageMapper.toMessageResponse(message);
                    lastMessageResponseList.add(new LastMessageResponse(bubbleResponse, messageResponse, status));
                });
            }
        }

        return lastMessageResponseList;
    }

    public boolean isMutualFollow(ObjectId user1, ObjectId user2) {
        Optional<Follow> follow1 = followRepository.findByUser1AndUser2AndStatus(user1, user2, FollowAction.ACCEPT);
        Optional<Follow> follow2 = followRepository.findByUser1AndUser2AndStatus(user2, user1, FollowAction.ACCEPT);
        return follow1.isPresent() && follow2.isPresent();
    }
}
