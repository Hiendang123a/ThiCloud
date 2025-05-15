package com.example.app01.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app01.DTO.Response.CommentResponse;
import com.example.app01.Google.GoogleDriveHelper;
import com.example.app01.R;

import de.hdodenhof.circleimageview.CircleImageView;

import java.util.Date;
import java.util.List;

public class RepliesAdapter extends RecyclerView.Adapter<RepliesAdapter.ReplyViewHolder> {

    private List<CommentResponse> replyList;
    public Context context;

    public RepliesAdapter(List<CommentResponse> replyList) {
        this.replyList = replyList;
    }

    @NonNull
    @Override
    public ReplyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_reply, parent, false);
        return new ReplyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReplyViewHolder holder, int position) {
        CommentResponse reply = replyList.get(position);
        holder.bind(reply);
    }

    @Override
    public int getItemCount() {
        return replyList.size();
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

        public void bind(CommentResponse reply) {
            usernameTextView.setText(reply.getName());
            contentTextView.setText(reply.getContent());
            timeTextView.setText(formatTime(reply.getCreatedAt()));
            if (reply.getAvatar() != null && !reply.getAvatar().isEmpty()) {
                GoogleDriveHelper.loadImage(itemView.getContext(), reply.getAvatar(), avatarImageView);
            } else {
                avatarImageView.setImageResource(R.drawable.default_avatar);
            }
        }

        private String formatTime(Date timestamp) {
            // Lấy thời gian từ timestamp dưới dạng mili giây
            long timeStampLong = timestamp.getTime();

            // Tính chênh lệch thời gian
            long diff = System.currentTimeMillis() - timeStampLong;
            long minutes = diff / (60 * 1000); // Tính số phút

            if (minutes < 1) return "Vừa xong";
            if (minutes < 60) return minutes + " phút trước";

            long hours = minutes / 60; // Tính số giờ
            if (hours < 24) return hours + " giờ trước";

            long days = hours / 24; // Tính số ngày
            return days + " ngày trước";
        }
    }
}
