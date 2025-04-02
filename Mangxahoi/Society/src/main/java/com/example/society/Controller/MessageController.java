package com.example.society.Controller;

import com.example.society.DTO.Request.CreateMessageRequest;
import com.example.society.DTO.Request.MarkSeenRequest;
import com.example.society.DTO.Request.ReadMessageRequest;
import com.example.society.DTO.Response.APIResponse;
import com.example.society.DTO.Response.LastMessageResponse;
import com.example.society.DTO.Response.MessageResponse;
import com.example.society.Service.Interface.MessageService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/message/")
@CrossOrigin(origins = "*") // Cho phép mọi nguồn gửi yêu cầu
@Validated
public class MessageController {

    @Autowired
    MessageService messageService;
    @PostMapping("create")
    public APIResponse<MessageResponse> createMessage(@Valid @RequestBody CreateMessageRequest createMessageRequest){
        String senderID = SecurityContextHolder.getContext().getAuthentication().getName();
        createMessageRequest.setSenderID(senderID);
        APIResponse<MessageResponse> apiResponse = new APIResponse<>();
        apiResponse.setResult(messageService.createMessage(createMessageRequest));
        return apiResponse;
    }

    @GetMapping("lastMessage")
    public APIResponse<List<LastMessageResponse>> lastMessage(){
        String userID = SecurityContextHolder.getContext().getAuthentication().getName();
        APIResponse<List<LastMessageResponse>> apiResponse = new APIResponse<>();
        apiResponse.setResult(messageService.lastMessage(userID));
        return apiResponse;
    }

    //doc tat ca tin nhan cua minh
    @PostMapping("read")
    public APIResponse<List<MessageResponse>> readMessages(@RequestBody ReadMessageRequest readMessageRequest) {
        String senderID = SecurityContextHolder.getContext().getAuthentication().getName();
        readMessageRequest.setSenderID(senderID);
        APIResponse<List<MessageResponse>> apiResponse = new APIResponse<>();
        apiResponse.setResult(messageService.readMessages(readMessageRequest));
        return apiResponse;
    }

    @PutMapping("markseen")
    public APIResponse<Void> markMessageAsSeen(@RequestBody MarkSeenRequest markSeenRequest)
    {
        String senderID = SecurityContextHolder.getContext().getAuthentication().getName();
        markSeenRequest.setSenderID(senderID);
        APIResponse<Void> apiResponse = new APIResponse<>();
        messageService.markMessagesAsSeen(markSeenRequest);
        return apiResponse;
    }

    @PutMapping("/delete/{messageID}")
    public APIResponse<Void> deleteMessage(@PathVariable String messageID)
    {
        APIResponse<Void> apiResponse = new APIResponse<>();
        messageService.softDeleteMessage(messageID);
        return apiResponse;
    }
}
