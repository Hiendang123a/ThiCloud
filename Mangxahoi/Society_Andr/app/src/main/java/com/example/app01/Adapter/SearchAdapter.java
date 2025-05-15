package com.example.app01.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.app01.Activity.ProfileUIActivity;
import com.example.app01.DTO.Response.BubbleResponse;
import com.example.app01.Google.GoogleDriveHelper;
import com.example.app01.R;

import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {
    private List<BubbleResponse> userList;
    private Context context;

    public SearchAdapter(Context context, List<BubbleResponse> userList) {
        this.context = context;
        this.userList = userList;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_search_user, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BubbleResponse user = userList.get(position);
        holder.txtUsername.setText(user.getName());
        if(user.getAvatar()!= null && !user.getAvatar().isEmpty())
        {
            GoogleDriveHelper.loadImage(context,user.getAvatar(),holder.profileImage);
        }
        else
        {
            holder.profileImage.setImageResource(R.drawable.default_avatar);
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ProfileUIActivity.class);
            intent.putExtra("username", user.getUserID());
            context.startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView profileImage;
        TextView txtUsername;

        public ViewHolder(View itemView) {
            super(itemView);
            profileImage = itemView.findViewById(R.id.profileImage);
            txtUsername = itemView.findViewById(R.id.txtUsername);
        }
    }
}