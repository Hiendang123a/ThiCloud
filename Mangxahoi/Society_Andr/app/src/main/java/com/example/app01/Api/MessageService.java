package com.example.app01.Api;

import com.example.app01.DTO.Request.CreateMessageRequest;
import com.example.app01.DTO.Request.MarkSeenRequest;
import com.example.app01.DTO.Request.ReadMessageRequest;
import com.example.app01.DTO.Response.APIResponse;
import com.example.app01.DTO.Response.LastMessageResponse;
import com.example.app01.DTO.Response.MessageResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;

public interface MessageService {
    @GET("/api/message/lastMessage")
    Call<APIResponse<List<LastMessageResponse>>> lastMessage();

    @POST("/api/message/read")
    Call<APIResponse<List<MessageResponse>>> readMessages(@Body ReadMessageRequest readMessageRequest);
    @PUT("/api/message/markseen")
    Call<APIResponse<Void>> markMessageAsSeen(@Body MarkSeenRequest markSeenRequest);
}
