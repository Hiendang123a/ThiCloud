package com.example.app01.Activity;

import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app01.Adapter.CommentAdapter;
import com.example.app01.Api.PostService;
import com.example.app01.Api.RetrofitClient;
import com.example.app01.DTO.Request.CommentRequest;
import com.example.app01.DTO.Response.APIResponse;
import com.example.app01.DTO.Response.CommentResponse;
import com.example.app01.R;
import com.example.app01.Utils.TokenManager;
import com.example.app01.WebSocket.WebSocketManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommentActivity extends AppCompatActivity {
    private CommentAdapter commentAdapter;
    private final List<CommentResponse> commentList = new ArrayList<>();
    private EditText editTextComment;
    private String postId;
    private WebSocketManager webSocketManager;
    private RecyclerView recyclerViewComments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_comment);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Lấy postId
        postId = getIntent().getStringExtra("postID");
        if (postId == null || postId.isEmpty()) {
            Toast.makeText(this, "Bài viết không tồn tại", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // WebSocket
        webSocketManager = new WebSocketManager();
        webSocketManager.connectWebSocket();

        // Ánh xạ view
        recyclerViewComments = findViewById(R.id.recyclerViewComments);
        editTextComment = findViewById(R.id.editTextComment);
        ImageView btnSendComment = findViewById(R.id.btnSendComment);

        // Setup RecyclerView
        recyclerViewComments.setLayoutManager(new LinearLayoutManager(this));
        commentAdapter = new CommentAdapter(this, commentList);
        recyclerViewComments.setAdapter(commentAdapter);

        // Load bình luận từ server
        fetchComments();

        // Gửi bình luận
        btnSendComment.setOnClickListener(v -> {
            String text = editTextComment.getText().toString().trim();
            if (text.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập bình luận", Toast.LENGTH_SHORT).show();
                return;
            }
            sendComment(text);
            editTextComment.setText(""); // Xóa ô nhập sau khi gửi
        });

        // Nhận bình luận qua WebSocket
        webSocketManager.subscribeToPostComments(postId, new WebSocketManager.PostCommentListener() {
            @Override
            public void onPostCommentReceived(CommentResponse comment) {
                runOnUiThread(() -> {
                    if (!commentList.contains(comment)) { // Tránh trùng
                        commentList.add(comment);
                        commentAdapter.notifyItemInserted(commentList.size() - 1);
                        recyclerViewComments.scrollToPosition(commentList.size() - 1);
                    } else {
                        Log.d("WebSocket", "Đã tồn tại comment, bỏ qua");
                    }
                });
            }
        });
    }

    private void fetchComments() {
        PostService postService = RetrofitClient.getRetrofitInstance(this).create(PostService.class);
        Call<APIResponse<List<CommentResponse>>> call = postService.getCommentByPostID(postId);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<APIResponse<List<CommentResponse>>> call, @NonNull Response<APIResponse<List<CommentResponse>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    APIResponse<List<CommentResponse>> apiResponse = response.body();
                    if (apiResponse.getResult() != null) {
                        commentList.clear();
                        commentList.addAll(apiResponse.getResult());
                        commentAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(CommentActivity.this, "Không có bình luận", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e("CommentActivity", "Lỗi tải bình luận: " + response.code() + " - " + response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<APIResponse<List<CommentResponse>>> call, @NonNull Throwable t) {
                Toast.makeText(CommentActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
                Log.e("CommentActivity", "Kết nối thất bại", t);
            }
        });
    }

    private void sendComment(String content) {
        CommentRequest commentRequest = new CommentRequest(postId, "", TokenManager.getInstance(getApplicationContext()).getUserID(), content);
        boolean sent = webSocketManager.sendCommentPost(commentRequest);
        if (!sent) {
            Toast.makeText(this, "Gửi bình luận thất bại", Toast.LENGTH_SHORT).show();
            Log.e("WebSocket", "Không thể gửi bình luận (kết nối lỗi)");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (webSocketManager != null) {
            webSocketManager.disconnectWebSocket();
        }
    }
}