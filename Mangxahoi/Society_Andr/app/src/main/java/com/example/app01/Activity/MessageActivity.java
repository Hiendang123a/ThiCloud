package com.example.app01.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app01.Adapter.MessageAdapter;
import com.example.app01.Api.MessageService;
import com.example.app01.Api.RetrofitClient;
import com.example.app01.DTO.Request.CreateMessageRequest;
import com.example.app01.DTO.Request.MarkSeenRequest;
import com.example.app01.DTO.Request.ReadMessageRequest;
import com.example.app01.DTO.Response.APIResponse;
import com.example.app01.DTO.Response.MessageResponse;
import com.example.app01.Google.GoogleDriveHelper;
import com.example.app01.R;
import com.example.app01.Utils.TimeFormatter;
import com.example.app01.Utils.TokenManager;
import com.example.app01.WebSocket.WebSocketManager;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MessageActivity extends AppCompatActivity {
    private WebSocketManager webSocketManager;
    private ActivityResultLauncher<Intent> pickImageLauncher;
    private CircleImageView img_back, img_avatar, img_send;
    private TextView txt_user_name;
    private RecyclerView rc_chatMessage;
    private EditText edt_message;
    private List<MessageResponse> messageResponseList;
    private ImageView img_attach;
    private MessageAdapter messageAdapter;
    private MessageService messageService;
    private Date cursor = null;
    private static final int LIMIT = 20;
    private boolean isLoading = false; // Tránh gọi API nhiều lần

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        webSocketManager = new WebSocketManager();
        webSocketManager.connectWebSocket();

        webSocketManager.setMessageListener(messageResponse -> runOnUiThread(() -> {
            messageResponseList.add(0, messageResponse);
            messageAdapter.notifyItemInserted(0);
            rc_chatMessage.scrollToPosition(0);
            edt_message.setText("");
            if(TokenManager.getInstance(getApplicationContext()).getUserID().equals(messageResponse.getReceiverID()))
                markSeen(new MarkSeenRequest(null,messageResponse.getSenderID()));
        }));

        webSocketManager.subscribeToMessages(TokenManager.getInstance(getApplicationContext()).getUserID());
        Mapping();

        String receiverID = getIntent().getStringExtra("receiverID");
        String receiverName = getIntent().getStringExtra("receiverName");
        String receiverAvt = getIntent().getStringExtra("receiverAvt");
        txt_user_name.setText(receiverName);
        if(receiverAvt != null && !receiverAvt.isEmpty())
            GoogleDriveHelper.loadImage(this,receiverAvt,img_avatar);

        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        if (result.getData().getData() != null) {
                            contentOrImage(receiverID,null,result.getData().getData());
                        }
                    }
                });

        img_send.setOnClickListener(v->{
            if(!edt_message.getText().toString().isEmpty())
            {
                contentOrImage(receiverID,edt_message.getText().toString(),null);
            }
        });

        img_back.setOnClickListener(v->{
            Intent intent = new Intent(this, ChatActivity.class);
            startActivity(intent);
        });

        img_attach.setOnClickListener(v->{
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            pickImageLauncher.launch(Intent.createChooser(intent, "Chọn ảnh"));
        });

        messageService = RetrofitClient.getRetrofitInstance(getApplicationContext()).create(MessageService.class);
        messageResponseList = new ArrayList<>();
        messageAdapter = new MessageAdapter(this,messageResponseList,receiverID,receiverAvt);
        rc_chatMessage.setAdapter(messageAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true);
        //layoutManager.setStackFromEnd(true);
        rc_chatMessage.setLayoutManager(layoutManager);

        rc_chatMessage.addOnScrollListener(new RecyclerView.OnScrollListener() {
            private boolean isFirstLoad = true;
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                int lastVisibleItem = layoutManager.findLastVisibleItemPosition();
                int totalItemCount = layoutManager.getItemCount();

                if (isFirstLoad) {
                    isFirstLoad = false;
                    return;
                }

                if (lastVisibleItem == totalItemCount - 1 && !isLoading && hasMoreData) {
                    getMessages(receiverID);
                }
            }
        });
        getMessages(receiverID);
        markSeen(new MarkSeenRequest(null,receiverID));
    }

    private boolean hasMoreData = true;
    private void getMessages(String receiverID) {
        if (isLoading || !hasMoreData) return;
        isLoading = true;

        ReadMessageRequest readMessageRequest = new ReadMessageRequest(null, receiverID, TimeFormatter.formatDate(cursor), LIMIT);
        Call<APIResponse<List<MessageResponse>>> call = messageService.readMessages(readMessageRequest);

        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<APIResponse<List<MessageResponse>>> call,@NonNull Response<APIResponse<List<MessageResponse>>> response) {
                isLoading = false;
                if (response.isSuccessful() && response.body() != null) {
                    List<MessageResponse> newMessages = response.body().getResult();

                    if (newMessages == null || newMessages.isEmpty()) {
                        hasMoreData = false;
                    } else {
                        int previousSize = messageResponseList.size();
                        messageResponseList.addAll(newMessages);
                        messageAdapter.notifyItemRangeInserted(previousSize, newMessages.size());

                        if (previousSize == 0) {
                            rc_chatMessage.post(() -> rc_chatMessage.scrollToPosition(0));
                        } else {
                            rc_chatMessage.post(() ->rc_chatMessage.scrollToPosition(previousSize - 1));
                        }
                        cursor = newMessages.get(newMessages.size() - 1).getSentAt();
                    }
                } else {
                    TokenManager.getInstance(getApplicationContext()).handleErrorResponse(response, () -> getMessages(receiverID));
                }
            }

            @Override
            public void onFailure(@NonNull Call<APIResponse<List<MessageResponse>>> call, @NonNull Throwable t) {
                isLoading = false;
                Toast.makeText(MessageActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void contentOrImage(String receiverID, String content, Uri imageUri){
        CreateMessageRequest createMessageRequest = new CreateMessageRequest(TokenManager.getInstance(getApplicationContext()).getUserID(),receiverID,content,null);
        if (imageUri != null) {
            GoogleDriveHelper googleDriveHelper = new GoogleDriveHelper(MessageActivity.this);
            googleDriveHelper.uploadFileToSociety(imageUri, "image/jpg", () -> {
                if (googleDriveHelper.getFileID() != null) {
                    createMessageRequest.setImageUrl(googleDriveHelper.getFileID());
                    webSocketManager.sendMessage(createMessageRequest);
                } else {
                    Log.e("GoogleDriveHelper", "Upload failed: File ID is null");
                    runOnUiThread(() ->
                            Toast.makeText(MessageActivity.this, "Upload failed!", Toast.LENGTH_SHORT).show()
                    );
                }
            });
        }else{
            webSocketManager.sendMessage(createMessageRequest);
        }
    }
    private void markSeen(MarkSeenRequest markSeenRequest) {
        messageService.markMessageAsSeen(markSeenRequest).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<APIResponse<Void>> call,@NonNull Response<APIResponse<Void>> response) {
                if (response.isSuccessful()) {
                    Log.d("Danh dau da xem", "Đánh dấu đã xem thành công");
                } else {
                    TokenManager.getInstance(getApplicationContext()).handleErrorResponse(response, () -> markSeen(markSeenRequest));
                }
            }
            @Override
            public void onFailure(@NonNull Call<APIResponse<Void>> call,@NonNull Throwable t) {
                Toast.makeText(MessageActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void Mapping(){
        img_back = findViewById(R.id.img_back);
        img_avatar = findViewById(R.id.img_avatar);
        img_attach = findViewById(R.id.img_attach);
        img_send = findViewById(R.id.img_send);
        txt_user_name = findViewById(R.id.txt_user_name);
        rc_chatMessage = findViewById(R.id.rc_chatMessage);
        edt_message = findViewById(R.id.edt_message);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        webSocketManager.disconnectWebSocket();
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, ChatActivity.class);
        startActivity(intent);
        super.onBackPressed();
    }
}