package com.example.app01.WebSocket;


import android.util.Log;

import com.example.app01.DTO.Request.CreateMessageRequest;
import com.example.app01.DTO.Response.MessageResponse;
import com.google.gson.Gson;
import io.reactivex.disposables.Disposable;
import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.StompClient;


public class WebSocketManager {
    private static final String TAG = "WebSocketManager";
    //private static final String WS_URL = "ws://10.0.2.2:8080/ws";
    private static final String WS_URL = "ws://192.168.28.175:8080/ws"; // WebSocket URL
    private StompClient stompClient;
    private Disposable topicSubscription;
    private MessageListener messageListener;

    public void connectWebSocket() {
        stompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, WS_URL);
        stompClient.connect();
    }

    public void subscribeToMessages(String myID) {
        topicSubscription = stompClient.topic("/user/" + myID + "/queue/messages")
                .subscribe(message -> {
                    Gson gson = new Gson();
                    MessageResponse messageResponse = gson.fromJson(message.getPayload(), MessageResponse.class);
                    if (messageListener != null) {
                        messageListener.onMessageReceived(messageResponse); // Gửi tin nhắn về Activity
                    }
                    }, throwable -> Log.e(TAG, "Error in Subscription", throwable));
    }
    public void sendMessage(CreateMessageRequest createMessageRequest) {
        Gson gson = new Gson();
        String jsonMessage = gson.toJson(createMessageRequest);

        String destination = "/app/sendPrivateMessage/" + createMessageRequest.getReceiverID(); // Gửi đến user cụ thể

        stompClient.send(destination, jsonMessage).subscribe();
    }

    public void disconnectWebSocket() {
        if (stompClient != null) {
            stompClient.disconnect();
        }
        if (topicSubscription != null) {
            topicSubscription.dispose();
        }
    }

    public interface MessageListener {
        void onMessageReceived(MessageResponse messageResponse);
    }

    public MessageListener getMessageListener() {
        return messageListener;
    }

    public void setMessageListener(MessageListener messageListener) {
        this.messageListener = messageListener;
    }
}
