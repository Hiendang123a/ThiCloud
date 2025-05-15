package com.example.app01.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.app01.DTO.Response.BubbleResponse;
import com.example.app01.Google.GoogleDriveHelper;
import com.example.app01.R;

import java.util.List;

public class FollowerAdapter extends RecyclerView.Adapter<FollowerAdapter.ViewHolder> {
    private final Context context;
    private List<BubbleResponse> followerList;
    private final OnProfileClickListener profileClickListener;
    private final OnFollowerActionListener removeClickListener;

    public FollowerAdapter(Context context, List<BubbleResponse> followerList, OnProfileClickListener listener, OnFollowerActionListener removeListener) {
        this.context = context;
        this.followerList = followerList;
        this.profileClickListener = listener;
        this.removeClickListener = removeListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_follower, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BubbleResponse follow = followerList.get(position);
        holder.txt_username.setText(follow.getName());
        if(follow.getAvatar() != null && !follow.getAvatar().isEmpty()){
            GoogleDriveHelper.loadImage(context,follow.getAvatar(),holder.imgAvatar);
        }
        else
        {
            holder.imgAvatar.setImageResource(R.drawable.default_avatar);
        }
        // ✅ Click vào avatar -> mở profile
        holder.imgAvatar.setOnClickListener(v -> {
            if (profileClickListener != null) {
                profileClickListener.onProfileClick(follow.getName());
            }
        });

        //bat su kien xu ly remove
        holder.btnRemove.setOnClickListener( v -> removeClickListener.onRemoveClick(follow));

    }

    @Override
    public int getItemCount() {
        return followerList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgAvatar;
        TextView  txt_username;
        Button btnRemove;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgAvatar = itemView.findViewById(R.id.profileImage);
            txt_username = itemView.findViewById(R.id.txtUsername);
            btnRemove = itemView.findViewById(R.id.btnRemove);
        }
    }
    public void setData(List<BubbleResponse> newFollowerList) {
        this.followerList = newFollowerList;
        notifyDataSetChanged(); // Cập nhật giao diện RecyclerView
    }

    public void removeFollow(BubbleResponse follow) {
        int position = followerList.indexOf(follow);
        if (position != -1) {
            followerList.remove(position);
            notifyItemRemoved(position);
        }
    }
    public interface OnFollowerActionListener {
        void onRemoveClick(BubbleResponse follow);
    }
    public interface OnProfileClickListener {
        void onProfileClick(String username);
    }
}

