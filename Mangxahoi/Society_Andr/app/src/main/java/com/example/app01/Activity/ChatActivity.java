package com.example.app01.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.app01.Adapter.BubbleAdapter;
import com.example.app01.Adapter.ChatAdapter;
import com.example.app01.Api.MessageService;
import com.example.app01.Api.RetrofitClient;
import com.example.app01.DTO.Response.APIResponse;
import com.example.app01.DTO.Response.BubbleResponse;
import com.example.app01.DTO.Response.LastMessageResponse;
import com.example.app01.R;
import com.example.app01.Utils.TokenManager;
import com.example.app01.WebSocket.WebSocketManager;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ChatActivity extends AppCompatActivity {
    private WebSocketManager webSocketManager;
    private Button logoutButton;
    public RecyclerView rc_bubbles, rc_messages;
    private BubbleAdapter bubbleAdapter;
    private ChatAdapter chatAdapter;
    private List<BubbleResponse> bubbleResponseList;

    private List<LastMessageResponse> lastMessageResponseList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        webSocketManager = new WebSocketManager();
        webSocketManager.connectWebSocket();
        webSocketManager.subscribeToMessages(TokenManager.getInstance(getApplicationContext()).getUserID());
        webSocketManager.setMessageListener(messageResponse -> runOnUiThread(() -> {
            for (LastMessageResponse lastMessageResponse : lastMessageResponseList) {
                if (lastMessageResponse.getBubbleResponse().getUserID().equals(messageResponse.getSenderID())) {
                    lastMessageResponse.setMessageResponse(messageResponse);
                    lastMessageResponseList.remove(lastMessageResponse);
                    lastMessageResponseList.add(0, lastMessageResponse);
                    chatAdapter.notifyDataSetChanged();
                    return;
                }
            }
        }));



        rc_bubbles = findViewById(R.id.rc_bubbles);
        rc_messages = findViewById(R.id.rc_messages);
        logoutButton = findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(v->{
            TokenManager tokenManager = TokenManager.getInstance(getApplicationContext());
            tokenManager.clearTokens();
            startActivity(new Intent(ChatActivity.this, MainActivity.class));
            finish();
        });
        Retrofit retrofit = RetrofitClient.getRetrofitInstance(getApplicationContext());
        bubbleResponseList = new ArrayList<>();
        bubbleAdapter = new BubbleAdapter(ChatActivity.this, bubbleResponseList);
        rc_bubbles.setAdapter(bubbleAdapter);
        rc_bubbles.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        lastMessageResponseList = new ArrayList<>();
        chatAdapter = new ChatAdapter(ChatActivity.this, lastMessageResponseList);

        rc_messages.setAdapter(chatAdapter);
        rc_messages.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        getLastMessage(retrofit);
    }

    private void getLastMessage(Retrofit retrofit){
        MessageService messageService = retrofit.create(MessageService.class);

        Call<APIResponse<List<LastMessageResponse>>> call = messageService.lastMessage();
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<APIResponse<List<LastMessageResponse>>> call,@NonNull Response<APIResponse<List<LastMessageResponse>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    APIResponse<List<LastMessageResponse>> apiResponse = response.body();
                    runOnUiThread(() -> {
                        List<BubbleResponse> bubbleResponses = apiResponse.getResult().stream()
                                .map(LastMessageResponse::getBubbleResponse)
                                .collect(Collectors.toList());
                        bubbleResponseList.clear();
                        bubbleResponseList.addAll(bubbleResponses);
                        bubbleAdapter.notifyDataSetChanged();
                        // Cập nhật danh sách tin nhắn
                        lastMessageResponseList.clear();
                        lastMessageResponseList.addAll(apiResponse.getResult());

                        // Kiểm tra danh sách không rỗng trước khi xóa phần tử đầu
                        if (!lastMessageResponseList.isEmpty()) {
                            lastMessageResponseList.remove(0);
                        }

                        chatAdapter.notifyDataSetChanged();

                        lastMessageResponseList.clear();
                        lastMessageResponseList.addAll(apiResponse.getResult());
                        lastMessageResponseList.remove(0);
                        chatAdapter.notifyDataSetChanged();
                    });
                } else {
                    try {
                        Gson gson = new Gson();
                        APIResponse<?> errorResponse = null;
                        if (response.errorBody() != null) {
                            errorResponse = gson.fromJson(response.errorBody().string(), APIResponse.class);
                        }
                        Toast.makeText(ChatActivity.this, "goi API that bai do loi token" + errorResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        if ("UNAUTHORIZED".equals(errorResponse.getHttpStatus())) {
                            TokenManager tokenManager = TokenManager.getInstance(getApplicationContext());
                            tokenManager.refreshToken(() -> runOnUiThread(() -> {
                                if (tokenManager.getAccessToken() != null && tokenManager.getRefreshToken() != null && tokenManager.getUserID() != null) {
                                    finish();
                                }
                            }));
                            Log.e("Vong lap Tin nhan chat","Dang lap lai");
                            getLastMessage(retrofit);
                        }
                    } catch (Exception e) {
                        Toast.makeText(ChatActivity.this, "Lỗi không xác định", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            @Override
            public void onFailure(@NonNull Call<APIResponse<List<LastMessageResponse>>> call,@NonNull Throwable t) {
                Toast.makeText(ChatActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("API Error", "Error message: " + t.getMessage(), t);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        webSocketManager.disconnectWebSocket();
    }
}