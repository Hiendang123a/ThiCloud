package com.example.society.Controller;

import com.example.society.DTO.Request.CreateMessageRequest;
import com.example.society.DTO.Response.MessageResponse;
import com.example.society.Service.Interface.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketController {
    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private MessageService messageService;
    // Nhận tin nhắn từ Client và gửi đến đúng người nhận
    @MessageMapping("/sendPrivateMessage/{receiverId}")
    public void sendPrivateMessage(@Payload CreateMessageRequest message, @DestinationVariable String receiverId) {
        String destination = "/user/" + receiverId + "/queue/messages";
        MessageResponse messageResponse = messageService.createMessage(message);
        messagingTemplate.convertAndSend(destination, messageResponse);
        messagingTemplate.convertAndSendToUser(message.getSenderID(), "/queue/messages", messageResponse);
    }
}

