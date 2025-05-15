package com.example.app01.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app01.Activity.CommentActivity;
import com.example.app01.Activity.LoginActivity;
import com.example.app01.Api.PostService;
import com.example.app01.Api.RetrofitClient;
import com.example.app01.Api.UserService;
import com.example.app01.DTO.Request.EmotionPostRequest;
import com.example.app01.DTO.Response.APIResponse;
import com.example.app01.DTO.Response.PostResponse;
import com.example.app01.DTO.Response.UserResponse;
import com.example.app01.Google.GoogleDriveHelper;
import com.example.app01.R;
import com.example.app01.Utils.TokenManager;
import com.example.app01.WebSocket.WebSocketManager;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import lombok.Getter;
import lombok.Setter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
public class PostAdapter extends RecyclerView.Adapter<PostAdapter.Post>{
    private final Context mContext;
    private final List<PostResponse> data;
    LayoutInflater layoutInflater;
    private final WebSocketManager webSocketManager;
    public PostAdapter(Context mContext, List<PostResponse> data, WebSocketManager webSocketManager) {
        this.mContext = mContext;
        this.data = data;
        this.webSocketManager = webSocketManager;
    }
    private final Map<String, UserResponse> userCache = new HashMap<>();

    @NonNull
    @Override
    public Post onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        layoutInflater = LayoutInflater.from(mContext);
        View itemView = layoutInflater.inflate(R.layout.item_reel,parent,false);return new Post(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull Post holder, int position) {
        PostResponse postResponse = data.get(position);
        String userID = postResponse.getUserID();
        UserResponse user = findUserInCacheOrData(userID);
        if (user != null) {
            bindUser(holder, user);
        } else {
            fetchUserFromAPI(holder, userID);
        }

        // Thiết lập các dữ liệu khác của bài viết
        holder.text_caption.setText(postResponse.getContent());
        holder.text_likes.setText(String.valueOf(postResponse.getEmotionsCount()));
        holder.text_view_comments.setText(String.valueOf(postResponse.getCommentsCount()));
        bindPost(holder,postResponse);
        // Cập nhật UI cho like/unlike
        setLikeIcon(holder, postResponse);

        // Xử lý sự kiện click like/unlike
        holder.icon_like.setOnClickListener(v -> handleLikeClick(holder, postResponse));

        // Hiển thị ảnh nếu có
        if (postResponse.getImageUrl() != null && !postResponse.getImageUrl().isEmpty()) {
            GoogleDriveHelper.loadImage(mContext, postResponse.getImageUrl(), holder.image_post);
        }

        holder.icon_comment.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, CommentActivity.class);
            intent.putExtra("postID", postResponse.getPostID());  // Truyền postID để có thể lấy bình luận của bài viết
            Log.d("Tag", postResponse.getPostID());
            mContext.startActivity(intent);
        });
    }


    private UserResponse findUserInCacheOrData(String userID) {
        // Kiểm tra user cache trước
        if (userCache.containsKey(userID)) {
            return userCache.get(userID); // Trả về UserResponse nếu có trong cache
        }
        // Kiểm tra data nếu có
        for (PostResponse post : data) {
            if (post.getUserID().equals(userID)) {
                return null; // Nếu không tìm thấy UserResponse trong cache, return null để gọi API
            }
        }
        return null; // Trả về null nếu không tìm thấy
    }

    private void fetchUserFromAPI(@NonNull Post holder, String userID) {
        UserService userService = RetrofitClient.getRetrofitInstance(mContext).create(UserService.class);
        Call<APIResponse<UserResponse>> call = userService.getUser(userID);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<APIResponse<UserResponse>> call, @NonNull Response<APIResponse<UserResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UserResponse user = response.body().getResult();
                    userCache.put(userID, user); // Lưu UserResponse vào cache
                    bindUser(holder, user); // Gọi bindUser khi dữ liệu đã được tải về
                }
            }

            @Override
            public void onFailure(@NonNull Call<APIResponse<UserResponse>> call, @NonNull Throwable t) {
            }
        });
    }

    private void setLikeIcon(@NonNull Post holder, PostResponse postResponse) {
        String userID = TokenManager.getInstance(mContext).getUserID();
        String postID = postResponse.getPostID();
        EmotionPostRequest emotionPostRequest = new EmotionPostRequest(postID, userID, "");
        PostService postService = RetrofitClient.getRetrofitInstance(mContext).create(PostService.class);
        postService.getEmotion(emotionPostRequest).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<APIResponse<String>> call, @NonNull Response<APIResponse<String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    APIResponse<String> apiResponse = response.body();
                    String emotion = apiResponse.getResult();
                    Log.d("LikeIcon", "API returned emotion: " + emotion);
                    if (postResponse.getEmotions().contains(emotion)) {
                        holder.icon_like.setImageResource(R.drawable.ic_love_full);
                    }
                    else {
                        holder.icon_like.setImageResource(R.drawable.ic_love);
                    }
                }
            }
            @Override
            public void onFailure(@NonNull Call<APIResponse<String>> call, @NonNull Throwable t) {
            }
        });

    }

    private void handleLikeClick(@NonNull Post holder, PostResponse postResponse) {
        String usrID = TokenManager.getInstance(mContext).getUserID();
        // Kiểm tra xem người dùng đã đăng nhập chưa
        if (usrID == null || usrID.isEmpty()) {
            Intent loginIntent = new Intent(mContext, LoginActivity.class);
            mContext.startActivity(loginIntent);
            return;
        }

        boolean isLiked = postResponse.getEmotions().contains(usrID);

        EmotionPostRequest emotionPostRequest;

        if (isLiked) {
            holder.icon_like.setImageResource(R.drawable.ic_love);
            postResponse.setEmotionsCount(postResponse.getEmotionsCount() - 1);
            postResponse.getEmotions().remove(usrID);
            emotionPostRequest = new EmotionPostRequest(postResponse.getPostID(), usrID, "unlike");
        } else {
            holder.icon_like.setImageResource(R.drawable.ic_love_full);
            postResponse.setEmotionsCount(postResponse.getEmotionsCount() + 1);
            postResponse.getEmotions().add(usrID);
            emotionPostRequest = new EmotionPostRequest(postResponse.getPostID(), usrID, "like");
        }

        holder.text_likes.setText(String.valueOf(postResponse.getEmotionsCount()));
        webSocketManager.sendPost(emotionPostRequest);
    }

    private void bindUser(Post holder, UserResponse user) {
        Log.d("bindUser", "Hiển thị user: " + user.getName() + ", avatar: " + user.getAvatar());
        holder.username.setText(user.getName());
        if (user.getAvatar() != null) {
            GoogleDriveHelper.loadImage(mContext, user.getAvatar(), holder.nav_profile);
        }
    }
    private void bindPost(Post holder, PostResponse postResponse) {
        // Định dạng thời gian theo kiểu bạn muốn (ví dụ: dd/MM/yyyy HH:mm)
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        String formattedDate = dateFormat.format(postResponse.getCreatedAt());
        holder.text_time.setText(formattedDate);
    }
    @Override
    public int getItemCount() {
        return data.size();
    }
    @Getter
    @Setter
    public static class Post extends RecyclerView.ViewHolder{
        private final CircleImageView nav_profile;
        private final TextView username, text_likes, text_caption, text_view_comments, text_time;
        private final ImageView icon_like, icon_comment, more_options,image_post;
        public Post(@NonNull View itemView) {
            super(itemView);
            this.nav_profile = itemView.findViewById(R.id.nav_profile);
            this.username = itemView.findViewById(R.id.username);
            this.text_likes = itemView.findViewById(R.id.text_likes);
            this.text_caption = itemView.findViewById(R.id.text_caption);
            this.text_view_comments = itemView.findViewById(R.id.text_view_comments);
            this.text_time = itemView.findViewById(R.id.text_time);
            this.icon_like = itemView.findViewById(R.id.icon_like);
            this.icon_comment = itemView.findViewById(R.id.icon_comment);
            this.more_options = itemView.findViewById(R.id.more_options);
            this.image_post = itemView.findViewById(R.id.image_post);
        }
    }
}

