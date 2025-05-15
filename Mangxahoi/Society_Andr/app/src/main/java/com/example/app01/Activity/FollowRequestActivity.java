package com.example.app01.Activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.util.Log;
import android.widget.Button;
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
import com.example.app01.Adapter.FollowRequestAdapter;
import com.example.app01.Api.FollowService;
import com.example.app01.Api.RetrofitClient;
import com.example.app01.DTO.Response.APIResponse;
import com.example.app01.DTO.Response.BubbleResponse;
import com.example.app01.DTO.Response.FollowResponse;
import com.example.app01.R;

import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Response;

public class FollowRequestActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FollowRequestAdapter adapter;
    private List<BubbleResponse> followRequests;
    private FollowService followService;
    private ImageView btnBack;
    private HandlerThread handlerThread;
    private Handler backgroundHandler;
    private Handler mainHandler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_follow_request);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.follow_request), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        followRequests = new ArrayList<>();
        //set adapter
        adapter = new FollowRequestAdapter(FollowRequestActivity.this, followRequests, new FollowRequestAdapter.OnFollowActionListener() {
            @Override
            public void onAccept(BubbleResponse request) {
                handleFollowAction(request, true);
            }

            @Override
            public void onReject(BubbleResponse request) {
                handleFollowAction(request, false);
            }
        });
        recyclerView.setAdapter(adapter);
        followService = RetrofitClient.getRetrofitInstance(getApplicationContext()).create(FollowService.class);
        // Khởi tạo HandlerThread để xử lý nền
        handlerThread = new HandlerThread("FollowRequestThread");
        handlerThread.start();
        backgroundHandler = new Handler(handlerThread.getLooper());
        mainHandler = new Handler(Looper.getMainLooper()); // Để cập nhật UI
        loadFollowRequests();
    }

    // 📌 Load danh sách follow request (chạy trong background)
    private void loadFollowRequests() {
        backgroundHandler.post(() -> {
            try {
                Call<APIResponse<List<BubbleResponse>>> call = followService.getFollowRequest();
                Response<APIResponse<List<BubbleResponse>>> response = call.execute(); // Chạy đồng bộ
                if (response.isSuccessful() && response.body() != null) {
                    APIResponse<List<BubbleResponse>> apiResponse = response.body();

                    // Cập nhật UI trên main thread
                    mainHandler.post(() -> {
                        followRequests.clear();
                        followRequests.addAll(apiResponse.getResult());
                        adapter.notifyDataSetChanged();
                    });
                }
            } catch (Exception e) {
                Log.e("FollowRequestActivity", "Lỗi khi tải danh sách: " + e.getMessage());
                mainHandler.post(() -> Toast.makeText(FollowRequestActivity.this, "Lỗi tải dữ liệu!", Toast.LENGTH_SHORT).show());
            }
        });
    }


    // 📌 Xử lý accept/reject follow request (chạy trong background)
    private void handleFollowAction(BubbleResponse request, boolean isAccepted) {
        String senderID = request.getUserID();
        Log.d("FollowRequestActivity", "Bắt đầu xử lý " + (isAccepted ? "chấp nhận" : "từ chối") + " follow request.");

        Call<APIResponse<FollowResponse>> call = isAccepted ?
                followService.acceptFollowRequest(senderID) :
                followService.rejectFollowRequest(senderID);
        call.enqueue(new retrofit2.Callback<>() {
            @Override
            public void onResponse(@NonNull Call<APIResponse<FollowResponse>> call, @NonNull Response<APIResponse<FollowResponse>> response) {
                try {
                    if (response.isSuccessful() && response.body() != null) {
                        APIResponse<FollowResponse> apiResponse = response.body();
                        Log.d("FollowRequestActivity", "Phản hồi thành công: " + apiResponse.getResult().getMessage());
                        Toast.makeText(FollowRequestActivity.this, apiResponse.getResult().getMessage(), Toast.LENGTH_SHORT).show();
                        loadFollowRequests(); // Tải lại danh sách
                    } else {
                        String errorMessage = response.errorBody() != null ? response.errorBody().string() : "Không có thông tin lỗi!";
                        Log.e("FollowRequestActivity", "Lỗi phản hồi API: " + errorMessage);
                        Toast.makeText(FollowRequestActivity.this, "Lỗi xử lý yêu cầu!", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Log.e("FollowRequestActivity", "Lỗi khi đọc phản hồi API: " + e.getMessage());
                }
            }
            @Override
            public void onFailure(@NonNull Call<APIResponse<FollowResponse>> call, @NonNull Throwable t) {
                Log.e("FollowRequestActivity", "Lỗi kết nối API: " + t.getMessage());
                Toast.makeText(FollowRequestActivity.this, "Lỗi kết nối đến server!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
