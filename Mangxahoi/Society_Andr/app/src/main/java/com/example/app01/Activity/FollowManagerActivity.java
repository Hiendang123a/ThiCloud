package com.example.app01.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app01.Adapter.FollowerAdapter;
import com.example.app01.Adapter.FollowingAdapter;
import com.example.app01.Api.FollowService;
import com.example.app01.Api.RetrofitClient;
import com.example.app01.DTO.Response.APIResponse;
import com.example.app01.DTO.Response.BubbleResponse;
import com.example.app01.DTO.Response.FollowResponse;
import com.example.app01.R;
import com.example.app01.ViewModel.FollowViewModel;
import com.google.android.material.tabs.TabLayout;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FollowManagerActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private String selected_tab;
    private FollowerAdapter followerAdapter;
    private FollowingAdapter followingAdapter;
    private FollowService followService;
    private ImageView btnBack;
    private TextView txtUsername;
    private FollowViewModel followViewModel;
    private int initialFollowerCount;
    private int initialFollowingCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_follow_manager);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.follow_manager), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        //anh xa
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());
        txtUsername = findViewById(R.id.txtUsername);
        txtUsername.setText(getIntent().getStringExtra("username"));
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager((new LinearLayoutManager(this)));
        //
        followService = RetrofitClient.getRetrofitInstance(getApplicationContext()).create(FollowService.class);

        followViewModel = new ViewModelProvider(this).get(FollowViewModel.class);
        followerAdapter = new FollowerAdapter(this, new ArrayList<>(), clickedUsername -> {
            Intent intent = new Intent(FollowManagerActivity.this, ProfileUIActivity.class);
            intent.putExtra("viewedUsername", clickedUsername);
            startActivity(intent);
        }, this::handleRemove);

        followingAdapter = new FollowingAdapter(this, new ArrayList<>(), clickedUsername -> {
            Intent intent = new Intent(FollowManagerActivity.this, ProfileUIActivity.class);
            intent.putExtra("viewedUsername", clickedUsername);
            startActivity(intent);
        }, this::handleUnfollow);


        //

        //get tab tuong ung
        selected_tab = getIntent().getStringExtra("SELECTED_TAB");

        // Kiểm tra nếu null, mặc định là "Followers"
        if (selected_tab == null || selected_tab.equalsIgnoreCase("followers")) {
            selected_tab = "Followers";
            recyclerView.setAdapter(followerAdapter);
            if (tabLayout.getTabAt(0) != null) {
                Objects.requireNonNull(tabLayout.getTabAt(0)).select(); // Chọn tab Followers
            }
        } else {
            selected_tab = "Following";
            recyclerView.setAdapter(followingAdapter);
            if (tabLayout.getTabAt(1) != null) {
                Objects.requireNonNull(tabLayout.getTabAt(1)).select(); // Chọn tab Following
            }
        }

        // Lắng nghe dữ liệu từ ViewModel
        followViewModel.getFollowers().observe(this, follows -> {
            if (selected_tab.equals("Followers")) {
                initialFollowerCount = follows.size();
                followerAdapter.setData(follows);
            }
        });

        followViewModel.getFollowing().observe(this, follows -> {
            if (selected_tab.equals("Following")) {
                initialFollowingCount = follows.size();
                followingAdapter.setData(follows);
            }
        });

        // Xử lý khi chuyển tab
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                selected_tab = Objects.requireNonNull(tab.getText()).toString();

                if (selected_tab.equalsIgnoreCase("Followers")) {
                    recyclerView.setAdapter(followerAdapter);
                    followViewModel.getFollowers().observe(FollowManagerActivity.this, followerAdapter::setData);
                } else {
                    recyclerView.setAdapter(followingAdapter);
                    followViewModel.getFollowing().observe(FollowManagerActivity.this, followingAdapter::setData);
                }
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}
            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void handleUnfollow(BubbleResponse follow) {
        String receiverID = follow.getUserID();
        Call<APIResponse<FollowResponse>> call = followService.unfollow(receiverID);

        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<APIResponse<FollowResponse>> call, @NonNull Response<APIResponse<FollowResponse>> response) {
                try {
                    if (response.isSuccessful() && response.body() != null) {
                        APIResponse<FollowResponse> apiResponse = response.body(); // Đọc nội dung phản hồi
                        Toast.makeText(FollowManagerActivity.this, apiResponse.getResult().getMessage(), Toast.LENGTH_SHORT).show();
                        followingAdapter.removeFollow(follow);
                    } else {
                        String errorMessage = response.errorBody() != null ? response.errorBody().string() : "Không có thông tin lỗi!";
                        Log.e("FollowManagerActivity", "Lỗi phản hồi unfollow API: " + errorMessage);
                        Toast.makeText(FollowManagerActivity.this, "Lỗi xử lý unfollow!", Toast.LENGTH_SHORT).show();
                    }
                } catch (IOException e) {
                    Log.e("FollowManagerActivity", "Lỗi khi đọc phản hồi API: " + e.getMessage());
                    Toast.makeText(FollowManagerActivity.this, "Lỗi khi đọc dữ liệu từ server!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<APIResponse<FollowResponse>> call, @NonNull Throwable t) {
                Log.e("FollowManagerActivity", "Lỗi kết nối unfollow API: " + t.getMessage());
                Toast.makeText(FollowManagerActivity.this, "Lỗi kết nối đến server!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleRemove(BubbleResponse follow) {
        String receiverID = follow.getUserID();

        Call<APIResponse<FollowResponse>> call = followService.removeFollower(receiverID);

        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<APIResponse<FollowResponse>> call, @NonNull Response<APIResponse<FollowResponse>> response) {
                try {
                    if (response.isSuccessful() && response.body() != null) {
                        APIResponse<FollowResponse> apiResponse = response.body(); // Đọc nội dung phản hồi
                        Toast.makeText(FollowManagerActivity.this, apiResponse.getResult().getMessage(), Toast.LENGTH_SHORT).show();
                        followerAdapter.removeFollow(follow);
                    } else {
                        String errorMessage = response.errorBody() != null ? response.errorBody().string() : "Không có thông tin lỗi!";
                        Log.e("FollowManagerActivity", "Lỗi phản hồi remove follower API: " + errorMessage);
                        Toast.makeText(FollowManagerActivity.this, "Lỗi xử lý remove follower!", Toast.LENGTH_SHORT).show();
                    }
                } catch (IOException e) {
                    Log.e("FollowManagerActivity", "Lỗi khi đọc phản hồi API: " + e.getMessage());
                    Toast.makeText(FollowManagerActivity.this, "Lỗi khi đọc dữ liệu từ server!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<APIResponse<FollowResponse>> call, @NonNull Throwable t) {
                Log.e("FollowManagerActivity", "Lỗi kết nối remove follower API: " + t.getMessage());
                Toast.makeText(FollowManagerActivity.this, "Lỗi kết nối đến server!", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onBackPressed() {
        int newFollowerCount = followerAdapter.getItemCount();
        int newFollowingCount = followingAdapter.getItemCount();
        Intent resultIntent = new Intent();
        boolean isUpdated = false;

        if (newFollowerCount != initialFollowerCount) {
            resultIntent.putExtra("UPDATED_FOLLOWER_COUNT", newFollowerCount);
            isUpdated = true;
        }

        if (newFollowingCount != initialFollowingCount) {
            resultIntent.putExtra("UPDATED_FOLLOWING_COUNT", newFollowingCount);
            isUpdated = true;
        }

        if (isUpdated) {
            setResult(RESULT_OK, resultIntent);
        }
        super.onBackPressed();
    }

}
