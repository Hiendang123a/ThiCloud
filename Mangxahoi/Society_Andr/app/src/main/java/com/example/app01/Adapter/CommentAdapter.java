    package com.example.app01.Adapter;

    import android.content.Context;
    import android.view.LayoutInflater;
    import android.view.View;
    import android.view.ViewGroup;
    import android.widget.TextView;

    import androidx.annotation.NonNull;
    import androidx.recyclerview.widget.LinearLayoutManager;
    import androidx.recyclerview.widget.RecyclerView;

    import com.example.app01.Api.PostService;
    import com.example.app01.Api.RetrofitClient;
    import com.example.app01.DTO.Response.APIResponse;
    import com.example.app01.DTO.Response.CommentResponse;
    import com.example.app01.Google.GoogleDriveHelper;
    import com.example.app01.R;

    import java.util.Date;
    import java.util.List;

    import de.hdodenhof.circleimageview.CircleImageView;
    import retrofit2.Call;
    import retrofit2.Callback;
    import retrofit2.Response;

    public class CommentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private static final int TYPE_COMMENT = 0;
        private static final int TYPE_REPLY = 1;
        private final List<CommentResponse> commentList;
        private final Context context;

        public CommentAdapter(Context context, List<CommentResponse> commentList) {
            this.context = context;
            this.commentList = commentList;
        }

        @Override
        public int getItemViewType(int position) {
            return commentList.get(position).getComments().isEmpty() ? TYPE_COMMENT : TYPE_REPLY;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if (viewType == TYPE_COMMENT) {
                View view = LayoutInflater.from(context).inflate(R.layout.item_comment, parent, false);
                return new CommentViewHolder(view);
            } else {
                View view = LayoutInflater.from(context).inflate(R.layout.item_reply, parent, false);
                return new ReplyViewHolder(view);
            }
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            CommentResponse comment = commentList.get(position);

            if (holder instanceof CommentViewHolder) {
                ((CommentViewHolder) holder).bind(comment);
            } else {
                ((ReplyViewHolder) holder).bind(comment);
            }
        }

        @Override
        public int getItemCount() {
            return commentList.size();
        }

        public static class CommentViewHolder extends RecyclerView.ViewHolder {
            TextView usernameTextView, contentTextView, timeTextView;
            RecyclerView repliesRecyclerView;
            CircleImageView avatarImageView;

            public CommentViewHolder(View itemView) {
                super(itemView);
                usernameTextView = itemView.findViewById(R.id.username);
                contentTextView = itemView.findViewById(R.id.content);
                repliesRecyclerView = itemView.findViewById(R.id.repliesRecyclerView);
                timeTextView = itemView.findViewById(R.id.time);
                avatarImageView = itemView.findViewById(R.id.avatar);
            }

            public void bind(CommentResponse comment) {
                usernameTextView.setText(comment.getName());
                contentTextView.setText(comment.getContent());
                timeTextView.setText(formatTime(comment.getCreatedAt()));
                if (comment.getAvatar() != null && !comment.getAvatar().isEmpty()) {
                    GoogleDriveHelper.loadImage(itemView.getContext(), comment.getAvatar(), avatarImageView);
                } else {
                    avatarImageView.setImageResource(R.drawable.default_avatar);
                }
                repliesRecyclerView.setLayoutManager(new LinearLayoutManager(itemView.getContext()));
                List<String> commentReply = comment.getComments();
                PostService postService = RetrofitClient.getRetrofitInstance(itemView.getContext()).create(PostService.class);
                postService.getComment(commentReply).enqueue(new Callback<>(){
                    @Override
                    public void onResponse(@NonNull Call<APIResponse<List<CommentResponse>>> call, @NonNull Response<APIResponse<List<CommentResponse>>> response) {
                        if(response.isSuccessful() && response.body()!=null){
                            APIResponse<List<CommentResponse>> apiResponse = response.body();
                            List<CommentResponse> data = apiResponse.getResult();
                            RepliesAdapter repliesAdapter = new RepliesAdapter(data);
                            repliesRecyclerView.setAdapter(repliesAdapter);
                        }
                    }
                    @Override
                    public void onFailure(@NonNull Call<APIResponse<List<CommentResponse>>> call, @NonNull Throwable t) {
                    }
                });

            }
        }

        public static class ReplyViewHolder extends RecyclerView.ViewHolder {
            TextView usernameTextView, contentTextView, timeTextView;
            CircleImageView avatarImageView;

            public ReplyViewHolder(View itemView) {
                super(itemView);
                usernameTextView = itemView.findViewById(R.id.username);
                contentTextView = itemView.findViewById(R.id.content);
                timeTextView = itemView.findViewById(R.id.time);
                avatarImageView = itemView.findViewById(R.id.avatar);
            }

            public void bind(CommentResponse comment) {
                usernameTextView.setText(comment.getName());
                contentTextView.setText(comment.getContent());
                timeTextView.setText(formatTime(comment.getCreatedAt()));
                if (comment.getAvatar() != null && !comment.getAvatar().isEmpty()) {
                    GoogleDriveHelper.loadImage(itemView.getContext(), comment.getAvatar(), avatarImageView);
                } else {
                    avatarImageView.setImageResource(R.drawable.default_avatar);
                }
            }
        }
        private static String formatTime(Date date) {
            long diff = System.currentTimeMillis() - date.getTime();
            long minutes = diff / (60 * 1000);
            if (minutes < 1) return "Vừa xong";
            if (minutes < 60) return minutes + " phút trước";
            long hours = minutes / 60;
            if (hours < 24) return hours + " giờ trước";
            long days = hours / 24;
            return days + " ngày trước";
        }
    }
