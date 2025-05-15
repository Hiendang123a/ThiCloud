package com.example.app01.Activity;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.app01.Adapter.GalleryAdapter;
import com.example.app01.Api.AccountService;
import com.example.app01.Api.FollowService;
import com.example.app01.Api.PostService;
import com.example.app01.Api.RetrofitClient;
import com.example.app01.Api.UserService;
import com.example.app01.DTO.Response.APIResponse;
import com.example.app01.DTO.Response.FollowResponse;
import com.example.app01.DTO.Response.UserResponse;
import com.example.app01.Google.GoogleDriveHelper;
import com.example.app01.R;
import com.example.app01.Utils.TokenManager;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileUIActivity extends AppCompatActivity {
    private GalleryAdapter galleryAdapter;
    private LinearLayout layoutContainer;
    private ImageView btn_menu, btnBack;
    private Button btnMessage, btnFollow;
    private CircleImageView img_avatar;
    private TextView txt_username, txt_bio, txt_followers, txt_following;
    private String viewedUsername;
    private PostService postService;
    private AccountService accountService;
    private FollowService followService;
    private UserService userService;
    private boolean isPrivate;
    private boolean isMe;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile_uiactivity);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.profile), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        RecyclerView recyclerView = findViewById(R.id.recyclerGallery);
        galleryAdapter = new GalleryAdapter(this, null);
        recyclerView.setAdapter(galleryAdapter);
        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());
        btn_menu = findViewById(R.id.btnMenu);
        btn_menu.setOnClickListener(v -> {
            View popupView = getLayoutInflater().inflate(R.layout.custom_popup_menu, null);
            PopupWindow popupWindow = new PopupWindow(popupView,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    true);

            // Xóa viền popup
            popupWindow.setBackgroundDrawable(null);

            // Hiển thị popup tại vị trí của btnMenu
            popupWindow.showAsDropDown(btn_menu);


            // Xử lý sự kiện click

            popupView.findViewById(R.id.btnEditProfile).setOnClickListener(view -> {
                Intent intent = new Intent(this, EditProfileActivity.class);
                startActivity(intent);
                popupWindow.dismiss();
            });

            popupView.findViewById(R.id.btnChangePassword).setOnClickListener(view -> {
                Intent intent = new Intent(ProfileUIActivity.this, RePassActivity.class);
                startActivity(intent);
                popupWindow.dismiss();
            });

            popupView.findViewById(R.id.btnLogout).setOnClickListener(view -> {
                Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show();
                TokenManager.getInstance(getApplicationContext()).clearTokens();

                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                popupWindow.dismiss();
            });
        });

        GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                int modPos = position % 8; // Lặp lại mỗi 8 ảnh

                if (modPos == 0) return 2; // Ảnh đầu tiên (lớn, chiếm 2 cột)
                else if (modPos == 1 || modPos == 2) return 1; // Hai ảnh nhỏ bên cạnh chiếm 1 cột
                else return 1; // 3 ảnh tiếp theo mỗi ảnh 1 cột
            }
        });


        recyclerView.setLayoutManager(layoutManager);
        postService = RetrofitClient.getRetrofitInstance(getApplicationContext()).create(PostService.class);
        accountService = RetrofitClient.getRetrofitInstance(getApplicationContext()).create(AccountService.class);
        followService = RetrofitClient.getRetrofitInstance(getApplicationContext()).create(FollowService.class);
        userService = RetrofitClient.getRetrofitInstance(getApplicationContext()).create(UserService.class);
        viewedUsername = getIntent().getStringExtra("username");
        if(viewedUsername == null || viewedUsername.isEmpty()){
            viewedUsername = "67effba14d241692ccbe530f";
        }
        //anh xa
        img_avatar = findViewById(R.id.imgAvatar);
        txt_username = findViewById(R.id.txtUsername);
        txt_bio = findViewById(R.id.txtBio);
        txt_followers = findViewById(R.id.txtFollowers);
        txt_following = findViewById(R.id.txtFollowing);
        btnMessage= findViewById(R.id.btnMessage);
        btnFollow = findViewById(R.id.btnFollow);
        ImageView btn_notification = findViewById(R.id.btnNotification);
        layoutContainer = findViewById(R.id.layoutContainer);
        //get username
        isMe = TokenManager.getInstance(getApplicationContext()).getUserID().equals(viewedUsername);
        //
        getProfile(viewedUsername);

        checkPrivacyAndFollowStatus(viewedUsername);
        getFollowersCount(viewedUsername);
        getFollowingCount(viewedUsername);

        //go to follow request (icon tim)
        ImageView btn_follow_request = findViewById(R.id.btnFollowRequest);
        btn_follow_request.setOnClickListener(v-> {
            Intent intent = new Intent(ProfileUIActivity.this, FollowRequestActivity.class);
            startActivity(intent);
        });


        btn_notification.setOnClickListener(v-> {
            Intent intent = new Intent(ProfileUIActivity.this, NotificationActivity.class);
            startActivity(intent);
        });


        //xu ly su kien click follower following
        txt_followers.setOnClickListener(v -> openFollowManager("followers"));
        txt_following.setOnClickListener(v -> openFollowManager("following"));
        //xu ly su kien click search
        ImageView navSearch = findViewById(R.id.nav_search);
        navSearch.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileUIActivity.this, SearchActivity.class);
            startActivity(intent);
        });
        // Xử lý sự kiện click btnFollow
        btnFollow.setOnClickListener(v -> accountService.checkPrivateStatus(viewedUsername).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<APIResponse<Boolean>> call, @NonNull Response<APIResponse<Boolean>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    APIResponse<Boolean> apiResponse = response.body();
                    isPrivate = apiResponse.getResult();
                    Log.d("DEBUG", "Tài khoản " + viewedUsername + " có private không? " + isPrivate);

                    boolean isCurrentlyFollowing = btnFollow.getText().toString().equals("Following");
                    boolean hasSentRequest = isPrivate; // Nếu là private, gửi request thay vì follow ngay

                    sendFollowRequest(viewedUsername,isCurrentlyFollowing, hasSentRequest);
                }
            }

            @Override
            public void onFailure(@NonNull Call<APIResponse<Boolean>> call, @NonNull Throwable t) {
                Log.e("API_ERROR1", "Lỗi lấy trạng thái private: " + t.getMessage());
            }
        }));
    }
    //
    private void loadGalleryImages(String userID) {
        postService.getUserPostImages(userID).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<APIResponse<List<String>>> call, @NonNull Response<APIResponse<List<String>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    APIResponse<List<String>> apiResponse = response.body();
                    List<String> imageUrls = apiResponse.getResult();
                    galleryAdapter.setImageUrls(imageUrls);
                } else {
                    Toast.makeText(ProfileUIActivity.this, "Không có ảnh nào!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<APIResponse<List<String>>> call, @NonNull Throwable t) {
                Log.e("GalleryError", "Lỗi API: " + t.getMessage());
                Toast.makeText(ProfileUIActivity.this, "Lỗi kết nối!", Toast.LENGTH_SHORT).show();
            }
        });
    }
    //
    private void getProfile(String userID)    {
        userService.getUser(userID).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<APIResponse<UserResponse>> call, @NonNull Response<APIResponse<UserResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    APIResponse<UserResponse> apiResponse = response.body();
                    UserResponse profile = apiResponse.getResult();
                    txt_username.setText(profile.getName());
                    txt_bio.setText(profile.getBio());
                    if(profile.getAvatar() != null && !profile.getAvatar().isEmpty()){
                        GoogleDriveHelper.loadImage(getApplicationContext(),profile.getAvatar(),img_avatar);
                    }
                    else
                    {
                        img_avatar.setImageResource(R.drawable.default_avatar);
                    }
                } else {
                    Log.e("API_ERROR", "API trả về lỗi: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<APIResponse<UserResponse>> call, @NonNull Throwable t) {
                Log.e("API_ERROR", "Lỗi kết nối API: " + t.getMessage(), t);
                Toast.makeText(ProfileUIActivity.this, "Lỗi kết nối API!" + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
    //
    private void getFollowersCount(String viewedID) {
        followService.getFollowersCount(viewedID).enqueue(new Callback<>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<APIResponse<Integer>> call, @NonNull Response<APIResponse<Integer>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    APIResponse<Integer> apiResponse = response.body();
                    int count = apiResponse.getResult();
                    Log.d("DEBUG", "Follower count: " + count);
                    txt_followers.setText(count + "\nFollowers");
                } else {
                    Log.e("API_ERROR", "API followers trả về lỗi: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<APIResponse<Integer>> call, @NonNull Throwable t) {
                Log.e("API_ERROR", "Lỗi khi gọi API followers", t);
            }
        });
    }
    //
    private void getFollowingCount(String viewedID) {
        followService.getFollowingCount(viewedID).enqueue(new Callback<>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<APIResponse<Integer>> call, @NonNull Response<APIResponse<Integer>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    APIResponse<Integer> apiResponse = response.body();
                    int count = apiResponse.getResult();
                    Log.d("DEBUG", "Following count: " + count);
                    txt_following.setText(count + "\nFollowing");
                } else {
                    Log.e("API_ERROR", "API following trả về lỗi: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<APIResponse<Integer>> call, @NonNull Throwable t) {
                Log.e("API_ERROR", "Lỗi khi gọi API following", t);
            }
        });
    }

    //xu ly su kien click tab bar
    private void openFollowManager(String selectedTab) {
        Intent intent = new Intent(this, FollowManagerActivity.class);
        intent.putExtra("SELECTED_TAB", selectedTab);
        intent.putExtra("username", viewedUsername);
        startActivityForResult(intent, 1001);
    }

    //
    private void checkPrivacyAndFollowStatus(String userID) {
        accountService.checkPrivateStatus(userID).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<APIResponse<Boolean>> call, @NonNull Response<APIResponse<Boolean>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    APIResponse<Boolean> apiResponse = response.body();
                    isPrivate = apiResponse.getResult();
                    Log.d("DEBUG", "Tài khoản " + userID + " có private không? " + isPrivate);

                    followService.isFollowing(userID).enqueue(new Callback<>() {
                        @Override
                        public void onResponse(@NonNull Call<APIResponse<Boolean>> call, @NonNull Response<APIResponse<Boolean>> response) {
                            APIResponse<Boolean> APIresponse = response.body();
                            boolean isFollowing = false;
                            if (APIresponse != null) {
                                isFollowing = response.isSuccessful() && APIresponse.getResult() != null && APIresponse.getResult();
                            }
                            Log.d("DEBUG", "User " + userID + " có theo dõi " + isFollowing);

                            // Tiếp tục xử lý giao diện
                            if (isMe) {
                                btnFollow.setVisibility(View.GONE);
                                btnMessage.setVisibility(View.GONE);
                                loadGalleryImages(userID);
                            } else {
                                if (isFollowing) {
                                    toggleFollowUI(true, isPrivate, false); // Đang theo dõi
                                } else {
                                    // Kiểm tra yêu cầu theo dõi
                                    followService.checkFollowRequestStatus(userID).enqueue(new Callback<>() {
                                        @Override
                                        public void onResponse(@NonNull Call<APIResponse<Boolean>> call, @NonNull Response<APIResponse<Boolean>> response) {
                                            APIResponse<Boolean> APIresponseLow = response.body();
                                            boolean hasSentRequest = response.isSuccessful() && APIresponseLow != null && APIresponseLow.getResult();
                                            Log.d("DEBUG", "User " + userID + " đã gửi request follow? " + hasSentRequest);
                                            toggleFollowUI(false, isPrivate, hasSentRequest);
                                        }

                                        @Override
                                        public void onFailure(@NonNull Call<APIResponse<Boolean>> call, @NonNull Throwable t) {
                                            Log.e("API_ERROR", "Lỗi kiểm tra follow request: " + t.getMessage());
                                        }
                                    });
                                }

                                // Set giao diện với trạng thái private
                                if (isPrivate && !isFollowing) {
                                    layoutContainer.setVisibility(View.GONE);
                                    btnMessage.setVisibility(View.GONE);
                                    @SuppressLint("InflateParams") View privateView = getLayoutInflater().inflate(R.layout.item_private_account, null);
                                    layoutContainer.addView(privateView);
                                    layoutContainer.setVisibility(View.VISIBLE);
                                    txt_followers.setEnabled(false);
                                    txt_following.setEnabled(false);
                                } else {
                                    layoutContainer.setVisibility(View.VISIBLE);
                                    btnMessage.setVisibility(View.VISIBLE);
                                    loadGalleryImages(userID);
                                }
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<APIResponse<Boolean>> call, @NonNull Throwable t) {
                            Log.e("API_ERROR", "Lỗi kiểm tra follow: " + t.getMessage());
                        }
                    });
                } else {
                    Log.e("API_ERROR1", "Không thành công khi lấy trạng thái private: " + response.code());
                    // In thông báo lỗi chi tiết
                    Log.e("API_ERROR", "Lỗi API private: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<APIResponse<Boolean>> call, @NonNull Throwable t) {
                Log.e("API_ERROR", "Lỗi API private: " + t.getMessage());
                Toast.makeText(getApplicationContext(), "Lỗi kết nối API!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null) {
            if (data.hasExtra("UPDATED_FOLLOWER_COUNT")) {
                int newFollowerCount = data.getIntExtra("UPDATED_FOLLOWER_COUNT", 0);
                txt_followers.setText(newFollowerCount + "\nFollower"); // Cập nhật giao diện
            }
            if (data.hasExtra("UPDATED_FOLLOWING_COUNT")) {
                int newFollowingCount = data.getIntExtra("UPDATED_FOLLOWING_COUNT", 0);
                txt_following.setText(newFollowingCount + "\nFollowing"); // Cập nhật giao diện
            }
        }

    }
    @SuppressLint("SetTextI18n")
    private void toggleFollowUI(boolean isFollowing, boolean isPrivate, boolean hasSentRequest) {

        if (isFollowing) {
            btnFollow.setText("Following");
            btnFollow.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#EEEEEE")));
            btnFollow.setTextColor(Color.BLACK);
        } else if (isPrivate && hasSentRequest) {
            btnFollow.setText("Sent");
            btnFollow.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#EEEEEE")));
            btnFollow.setTextColor(Color.BLACK);
        } else {
            btnFollow.setText("Follow");
            btnFollow.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#007AFF")));
            btnFollow.setTextColor(Color.WHITE);
        }
    }
    private void sendFollowRequest(String userID, boolean wasFollowing, boolean hadSentRequest) {
        followService.sendFollowRequest(userID).enqueue(new Callback<>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<APIResponse<FollowResponse>> call, @NonNull Response<APIResponse<FollowResponse>> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    rollbackFollowUI(wasFollowing, hadSentRequest);
                    Toast.makeText(ProfileUIActivity.this, "Lỗi khi gửi yêu cầu follow!", Toast.LENGTH_SHORT).show();
                }
                else {
                    if (isPrivate) {
                        Log.d("DEBUG", "Cập nhật UI: Đã gửi request follow, hiển thị 'Sent'");
                        btnFollow.setText("Sent"); // Nếu tài khoản riêng tư, hiển thị "Sent";
                        btnFollow.setBackgroundColor(Color.parseColor("#EEEEEE"));
                    } else {
                        Log.d("DEBUG", "Cập nhật UI: Đã follow ngay, hiển thị 'Following'");
                        btnFollow.setText("Following"); // Nếu tài khoản public, follow ngay
                        btnFollow.setBackgroundColor(Color.parseColor("#EEEEEE"));
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<APIResponse<FollowResponse>> call, @NonNull Throwable t) {
                rollbackFollowUI(wasFollowing, hadSentRequest);
                Toast.makeText(ProfileUIActivity.this, "Lỗi kết nối đến server!", Toast.LENGTH_SHORT).show();
                Log.e("API_ERROR", "Lỗi gửi follow request: " + t.getMessage());
            }
        });
    }
    private void rollbackFollowUI(boolean wasFollowing, boolean hadSentRequest) {
        runOnUiThread(() -> toggleFollowUI(wasFollowing, isPrivate, hadSentRequest));
    }
}