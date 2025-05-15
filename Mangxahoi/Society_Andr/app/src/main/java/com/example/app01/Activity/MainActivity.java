package com.example.app01.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.app01.Adapter.PostAdapter;
import com.example.app01.Api.PostService;
import com.example.app01.Api.RetrofitClient;
import com.example.app01.DTO.Response.APIResponse;
import com.example.app01.DTO.Response.PostResponse;
import com.example.app01.R;
import com.example.app01.Utils.TokenManager;
import com.example.app01.WebSocket.WebSocketManager;

import java.util.ArrayList;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
@Slf4j
public class MainActivity extends AppCompatActivity {
    PostAdapter adapter;
    RecyclerView post;
    int page = 0; // bắt đầu từ 0
    boolean isLoading = false; // kiểm soát việc gọi API trùng lặp
    final int size = 10;
    List<PostResponse> data = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ImageView btn_image = findViewById(R.id.nav_profile);
        ImageView btn_shop = findViewById(R.id.nav_shop);
        ImageView btn_reels = findViewById(R.id.nav_reels);

        btn_image.setOnClickListener(v -> {
            checkTokenAndProceed(ProfileUIActivity.class);
        });

        btn_shop.setOnClickListener(v -> checkTokenAndProceed(ChatActivity.class));
        btn_reels.setOnClickListener(v -> checkTokenAndProceed(PostFeedActivity.class));
        post = findViewById(R.id.recycler_posts);
        WebSocketManager webSocketManager = new WebSocketManager();
        webSocketManager.connectWebSocket();
        webSocketManager.setPostListener(webSocket -> {
        });
        adapter = new PostAdapter(this,data,webSocketManager);
        post.setAdapter(adapter);
        post.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        post.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (layoutManager != null && !isLoading) {
                    int totalItemCount = layoutManager.getItemCount();
                    int lastVisibleItem = layoutManager.findLastVisibleItemPosition();

                    if (lastVisibleItem >= totalItemCount - 1) { // gần cuối danh sách
                        page++;
                        loadMoreData();
                    }
                }
            }
        });
        loadMoreData(); // tải trang đầu tiên
    }
    private void checkTokenAndProceed(Class<?> destinationActivity) {
        TokenManager tokenManager = TokenManager.getInstance(getApplicationContext());
        tokenManager.refreshToken(() -> runOnUiThread(() -> {
            if (tokenManager.getAccessToken() == null) {
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
            } else {
                if(destinationActivity==ProfileUIActivity.class)
                {
                    Intent intent = new Intent(MainActivity.this, destinationActivity);
                    intent.putExtra("username", TokenManager.getInstance(getApplicationContext()).getUserID()); // Truyền thêm dữ liệu
                    startActivity(intent);
                }
                else {
                    startActivity(new Intent(MainActivity.this, destinationActivity));
                }
            }
        }));
    }
    private void loadMoreData() {
        if (isLoading) return;

        isLoading = true;
        Log.d("LoadData", "Đang gọi page = " + page);

        TokenManager tokenManager = TokenManager.getInstance(getApplicationContext());
        Retrofit retrofit = RetrofitClient.getRetrofitInstance(getApplicationContext());
        PostService postService = retrofit.create(PostService.class);

        String userId = tokenManager.getAccessToken() == null ? null : tokenManager.getUserID();
        Call<APIResponse<List<PostResponse>>> call = postService.getHomeFeedPosts(userId, page, size);

        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<APIResponse<List<PostResponse>>> call, @NonNull Response<APIResponse<List<PostResponse>>> response) {
                isLoading = false;
                if (response.isSuccessful() && response.body() != null) {
                    List<PostResponse> newData = response.body().getResult();
                    if (newData != null) {
                        if (!newData.isEmpty()) {
                            Log.d("TAG", "Loaded data: " + newData.size() + " items");
                            data.addAll(newData);
                            adapter.notifyDataSetChanged();
                        } else {
                            Log.d("TAG", "No more data to load.");
                        }
                        page++;  // Luôn tăng page
                    }
                } else {
                    Log.e("LoadDataError", "Lỗi không thành công. Code: " + response.code() + " - " + response.message());
                    if (response.errorBody() != null) {
                        try {
                            String errorBody = response.errorBody().string();
                            Log.e("LoadDataError", "Chi tiết lỗi: " + errorBody);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<APIResponse<List<PostResponse>>> call, @NonNull Throwable t) {
                isLoading = false;
                Log.e("TAG", "Error loading data", t);
            }
        });
    }
}