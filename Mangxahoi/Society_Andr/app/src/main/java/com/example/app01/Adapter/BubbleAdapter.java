package com.example.app01.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app01.Activity.MessageActivity;
import com.example.app01.DTO.Response.BubbleResponse;
import com.example.app01.Google.GoogleDriveHelper;
import com.example.app01.R;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import lombok.Getter;
import lombok.Setter;

public class BubbleAdapter extends RecyclerView.Adapter<BubbleAdapter.BubbleViewHolder> {
    private final List<BubbleResponse> bubbleResponses;
    private final Context context;
    private final LayoutInflater layoutInflater;


    public BubbleAdapter(Context context, List<BubbleResponse> bubbleResponses) {
        this.bubbleResponses = bubbleResponses;
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public BubbleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = layoutInflater.inflate(R.layout.row_friend_img, parent, false);
        return new BubbleViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull BubbleViewHolder holder, int position) {
        BubbleResponse bubbleResponse = bubbleResponses.get(position);
        holder.getBubble_imgUser().setImageResource(R.drawable.default_avatar);
        if(position != 0) {
            String fullName = bubbleResponse.getName();
            String[] parts = fullName.trim().split("\\s+"); // Tách chuỗi theo khoảng trắng
            String lastName = parts[parts.length - 1]; // Lấy phần tử cuối cùng
            holder.getBubble_txtUser().setText(lastName);
        }
        if(!bubbleResponse.getAvatar().isEmpty()) {
            GoogleDriveHelper.loadImage(context,bubbleResponse.getAvatar(),holder.bubble_imgUser);
        }
    }


    @Override
    public int getItemCount() {
        return bubbleResponses.size();
    }

    @Setter
    @Getter
    public class BubbleViewHolder extends RecyclerView.ViewHolder{
        private CircleImageView bubble_imgUser;
        private TextView bubble_txtUser;

        public BubbleViewHolder(@NonNull View itemView) {
            super(itemView);
            bubble_imgUser = itemView.findViewById(R.id.bubble_imgUser);
            bubble_txtUser = itemView.findViewById(R.id.bubble_txtName);
            bubble_imgUser.setOnClickListener(v-> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && position != 0) {
                    BubbleResponse bubbleResponse = bubbleResponses.get(position);
                    Intent intent = new Intent(context, MessageActivity.class);
                    intent.putExtra("receiverID",bubbleResponse.getUserID());
                    intent.putExtra("receiverName",bubbleResponse.getName());
                    intent.putExtra("receiverAvt",bubbleResponse.getAvatar());
                    context.startActivity(intent);
                }
            });
        }

    }
}
