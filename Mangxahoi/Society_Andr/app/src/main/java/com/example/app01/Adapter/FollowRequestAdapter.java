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

public class FollowRequestAdapter extends RecyclerView.Adapter<FollowRequestAdapter.ViewHolder> {
    private final Context context;
    private final List<BubbleResponse> followRequests;
    private final OnFollowActionListener followActionListener;
    public FollowRequestAdapter(Context context, List<BubbleResponse> followRequests, OnFollowActionListener listener) {
        this.context = context;
        this.followRequests = followRequests;
        this.followActionListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_follow_request, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BubbleResponse request = followRequests.get(position);

        if(request.getAvatar() != null && !request.getAvatar().isEmpty()){
            GoogleDriveHelper.loadImage(context,request.getAvatar(),holder.avatarImageView);
        }
        else
        {
            holder.avatarImageView.setImageResource(R.drawable.default_avatar);
        }

        holder.usernameTextView.setText(request.getName());

        // Xử lý khi nhấn Accept hoặc Reject
        holder.btnAccept.setOnClickListener(v -> followActionListener.onAccept(request));
        holder.btnReject.setOnClickListener(v -> followActionListener.onReject(request));
    }

    @Override
    public int getItemCount() {
        return followRequests.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView avatarImageView;
        TextView usernameTextView;
        Button btnAccept, btnReject;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            avatarImageView = itemView.findViewById(R.id.profileImage);
            usernameTextView = itemView.findViewById(R.id.txtUsername);
            btnAccept = itemView.findViewById(R.id.btnConfirm);
            btnReject = itemView.findViewById(R.id.btnDelete);
        }
    }
    public interface OnFollowActionListener {
        void onAccept(BubbleResponse request);
        void onReject(BubbleResponse request);
    }
}