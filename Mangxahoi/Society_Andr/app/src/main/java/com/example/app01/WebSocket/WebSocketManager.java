package com.example.app01.WebSocket;


import android.util.Log;

import com.example.app01.DTO.Request.CommentRequest;
import com.example.app01.DTO.Request.CreateMessageRequest;
import com.example.app01.DTO.Request.EmotionPostRequest;
import com.example.app01.DTO.Response.CommentResponse;
import com.example.app01.DTO.Response.EmotionPostResponse;
import com.example.app01.DTO.Response.MessageResponse;
import com.google.gson.Gson;
import io.reactivex.disposables.Disposable;
import lombok.Getter;
import lombok.Setter;
import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.StompClient;


public class WebSocketManager {
    private static final String TAG = "WebSocketManager";
    //private static final String WS_URL = "ws://10.0.2.2:8080/ws";
    private static final String WS_URL = "ws://192.168.1.73:8080/ws"; // WebSocket URL
    private StompClient stompClient;
    private Disposable topicSubscription;
    @Setter
    @Getter
    private MessageListener messageListener;

    @Setter
    @Getter
    private PostListener postListener;

    @Setter
    @Getter
    private PostCommentListener postCommentListener;

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

    public void subscribeToPost(String myID) {
        topicSubscription = stompClient.topic("/user/" + myID + "/queue/sendEmotionPost")
                .subscribe(message -> {
                    Gson gson = new Gson();
                    EmotionPostResponse emotionPostResponse = gson.fromJson(message.getPayload(), EmotionPostResponse.class);
                    if (postListener != null) {
                        postListener.onPostReceived(emotionPostResponse);
                    }
                });
    }
    public void sendPost(EmotionPostRequest emotionPostRequest) {
        Gson gson = new Gson();
        String jsonMessage = gson.toJson(emotionPostRequest);
        String destination = "/app/sendEmotionPost/" + emotionPostRequest.getUserID(); // Gửi đến user cụ thể
        stompClient.send(destination, jsonMessage).subscribe();
    }

    public boolean sendCommentPost(CommentRequest commentRequest) {
        Gson gson = new Gson();
        String jsonMessage = gson.toJson(commentRequest);
        String destination = "/app/sendComment/" + commentRequest.getUserID(); // Gửi đến user cụ thể
        stompClient.send(destination, jsonMessage).subscribe();
        return true;
    }

    private Disposable commentSubscription; // Khai báo trong WebSocketManager

    public void subscribeToPostComments(String postId, PostCommentListener listener) {
        if (commentSubscription != null && !commentSubscription.isDisposed()) {
            // Đã subscribe rồi, không cần subscribe lại
            return;
        }

        commentSubscription = stompClient.topic("/user/comments/" + postId)
                .subscribe(message -> {
                    CommentResponse comment = new Gson().fromJson(message.getPayload(), CommentResponse.class);
                    if (listener != null) {
                        listener.onPostCommentReceived(comment);
                    }
                });
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
    public interface PostListener {
        void onPostReceived(EmotionPostResponse emotionPostResponse);
    }

    public interface PostCommentListener {
        void onPostCommentReceived(CommentResponse commentResponse);
    }

}
