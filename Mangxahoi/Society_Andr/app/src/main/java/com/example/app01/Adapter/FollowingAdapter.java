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

public class FollowingAdapter extends RecyclerView.Adapter<FollowingAdapter.ViewHolder> {
    private final Context context;
    private List<BubbleResponse> followingList;
    private final OnProfileClickListener profileClickListener;
    private final OnFollowingActionListener unfollowClickListener;

    public FollowingAdapter(Context context, List<BubbleResponse> followingList, OnProfileClickListener listener, OnFollowingActionListener unfollowListener) {
        this.context = context;
        this.followingList = followingList;
        this.profileClickListener = listener;
        this.unfollowClickListener = unfollowListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_following, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BubbleResponse follow = followingList.get(position);
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
        //bat su kien xu ly unfollow
        holder.btnUnfollow.setOnClickListener( v -> unfollowClickListener.onUnfollowClick(follow));
    }

    @Override
    public int getItemCount() {
        return followingList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgAvatar;
        TextView txt_username;
        Button btnUnfollow;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgAvatar = itemView.findViewById(R.id.profileImage);
            txt_username = itemView.findViewById(R.id.txtUsername);
            btnUnfollow = itemView.findViewById(R.id.btnUnfollow);
        }
    }
    public void setData(List<BubbleResponse> newFollowingList) {
        this.followingList = newFollowingList;
        notifyDataSetChanged(); // Cập nhật giao diện RecyclerView
    }
    public interface OnFollowingActionListener {
        void onUnfollowClick(BubbleResponse follow);
    }

    public interface OnProfileClickListener {
        void onProfileClick(String username);
    }
    public void removeFollow(BubbleResponse follow) {
        int position = followingList.indexOf(follow);
        if (position != -1) {
            followingList.remove(position);
            notifyItemRemoved(position);
        }
    }

}
