package com.example.society.Controller;

import com.example.society.DTO.Request.CommentRequest;
import com.example.society.DTO.Request.CreateMessageRequest;
import com.example.society.DTO.Request.EmotionCommentRequest;
import com.example.society.DTO.Request.EmotionPostRequest;
import com.example.society.DTO.Response.CommentResponse;
import com.example.society.DTO.Response.EmotionCommentResponse;
import com.example.society.DTO.Response.EmotionPostResponse;
import com.example.society.DTO.Response.MessageResponse;
import com.example.society.Service.Interface.CommentService;
import com.example.society.Service.Interface.EmotionService;
import com.example.society.Service.Interface.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketController {
    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private MessageService messageService;

    @Autowired
    private EmotionService emotionService;

    @Autowired
    private CommentService commentService;

    @MessageMapping("/sendPrivateMessage/{receiverId}")
    public void sendPrivateMessage(@Payload CreateMessageRequest message, @DestinationVariable String receiverId) {
        String destination = "/user/" + receiverId + "/queue/messages";
        MessageResponse messageResponse = messageService.createMessage(message);
        messagingTemplate.convertAndSend(destination, messageResponse);
        messagingTemplate.convertAndSendToUser(message.getSenderID(), "/queue/messages", messageResponse);
    }

    @MessageMapping("/sendEmotionPost/{receiverId}")
    public void sendEmotionPost(@Payload EmotionPostRequest emotionPostRequest,
                                 @DestinationVariable String receiverId) {
        String destination = "/user/" + receiverId + "/queue/sendEmotionPost";
        EmotionPostResponse emotionPostResponse = emotionService.emotionPost(emotionPostRequest);
        messagingTemplate.convertAndSend(destination, emotionPostRequest);
        messagingTemplate.convertAndSendToUser(emotionPostResponse.getUserID(), "/queue/sendEmotionPost", emotionPostResponse);
    }


    @MessageMapping("/sendEmotionComment/{receiverId}")
    public void sendEmotionComment(@Payload EmotionCommentRequest emotionCommentRequest,
                                   @DestinationVariable String receiverId) {
        String destination = "/user/" + receiverId + "/queue/sendEmotionComment";
        EmotionCommentResponse emotionCommentResponse = emotionService.emotionComment(emotionCommentRequest);
        messagingTemplate.convertAndSend(destination, emotionCommentRequest);
        messagingTemplate.convertAndSendToUser(emotionCommentResponse.getUserID(), "/queue/sendEmotionComment", emotionCommentResponse);
    }

    @MessageMapping("/sendComment/{receiverId}")
    public void sendComment(@Payload CommentRequest commentRequest,
                            @DestinationVariable String receiverId) {
        String destination = "/user/" + receiverId + "/queue/sendComment";
        CommentResponse commentResponse = commentService.comment(commentRequest);
        messagingTemplate.convertAndSend(destination, commentResponse);
        messagingTemplate.convertAndSendToUser(commentResponse.getUserID(), "/queue/sendComment", commentResponse);
    }
}

