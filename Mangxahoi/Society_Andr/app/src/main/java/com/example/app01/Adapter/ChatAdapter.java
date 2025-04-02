package com.example.app01.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app01.Activity.MessageActivity;
import com.example.app01.DTO.Response.LastMessageResponse;
import com.example.app01.Google.GoogleDriveHelper;
import com.example.app01.R;
import com.example.app01.Utils.TimeFormatter;
import com.example.app01.Utils.TokenManager;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import lombok.Getter;
import lombok.Setter;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MessageViewHolder> {
    private final Context context;
    private List<LastMessageResponse> data;
    private final LayoutInflater layoutInflater;
    private final TokenManager tokenManager;
    public ChatAdapter(Context context, List<LastMessageResponse> data) {
        this.context = context;
        this.data = data;
        this.layoutInflater = LayoutInflater.from(context);
        tokenManager = TokenManager.getInstance(context);
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView =  layoutInflater.inflate(R.layout.row_message_img,parent,false);
        return new MessageViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        LastMessageResponse lastMessageResponse = data.get(position);
        String name = lastMessageResponse.getBubbleResponse().getName();
        holder.getMessage_txtName().setText(name);

        if(lastMessageResponse.getBubbleResponse().getAvatar()!= null && !lastMessageResponse.getBubbleResponse().getAvatar().isEmpty())
        {
            GoogleDriveHelper.loadImage(context,lastMessageResponse.getBubbleResponse().getAvatar(),holder.message_imgUser);
        }
        else
        {
            holder.message_imgUser.setImageResource(R.drawable.default_avatar);
        }

        if(lastMessageResponse.getMessageResponse() == null)
            holder.getMessage_txtMessage().setText("Bạn và " + name + " đã trở thành bạn");
        else if (lastMessageResponse.getMessageResponse().isDeleted())
        {
            if(tokenManager.getUserID().equals(lastMessageResponse.getBubbleResponse().getUserID())){
                holder.getMessage_txtMessage().setText(name + " đã xóa tin nhắn");
            }
            else{
                holder.getMessage_txtMessage().setText("Bạn đã xóa tin nhắn");
            }
        }
        else if(lastMessageResponse.getMessageResponse().getContent() != null)
        {
            if(tokenManager.getUserID().equals(lastMessageResponse.getMessageResponse().getReceiverID())){
                holder.getMessage_txtMessage().setText(lastMessageResponse.getMessageResponse().getContent());
                if(lastMessageResponse.getMessageResponse().getSeenAt()==null)
                {
                    holder.getMessage_txtMessage().setTypeface(null, Typeface.BOLD);
                    holder.getMessage_txtMessage().setTextSize(16);
                }
            }
            else{
                holder.getMessage_txtMessage().setText("Bạn: " + lastMessageResponse.getMessageResponse().getContent());
            }
        }
        else
        {
            if(tokenManager.getUserID().equals(lastMessageResponse.getMessageResponse().getReceiverID())){
                holder.getMessage_txtMessage().setText(name + " đã gửi 1 hình ảnh");
                if(lastMessageResponse.getMessageResponse().getSeenAt()==null)
                    holder.getMessage_txtMessage().setTypeface(null, Typeface.BOLD);
            }
            else{
                holder.getMessage_txtMessage().setText("Bạn đã gửi 1 hình ảnh");
            }
        }
        if(lastMessageResponse.getMessageResponse() == null)
            holder.getMessage_txtTime().setText("");
        else
            holder.getMessage_txtTime().setText(TimeFormatter.formatTime(lastMessageResponse.getMessageResponse().getSentAt()));
    }
    @Override
    public int getItemCount() {
        return data.size();
    }
    @Setter
    @Getter
    public class MessageViewHolder extends RecyclerView.ViewHolder{
        private CircleImageView message_imgUser;
        private TextView message_txtName, message_txtMessage, message_txtTime;
        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            message_imgUser = itemView.findViewById(R.id.message_imgUser);
            message_txtName = itemView.findViewById(R.id.message_txtName);
            message_txtMessage = itemView.findViewById(R.id.message_txtMessage);
            message_txtTime = itemView.findViewById(R.id.message_txtTime);
            itemView.setOnClickListener(v->{
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    LastMessageResponse lastMessageResponse = data.get(position);
                    Intent intent = new Intent(context, MessageActivity.class);
                    intent.putExtra("receiverID",lastMessageResponse.getBubbleResponse().getUserID());
                    intent.putExtra("receiverName",lastMessageResponse.getBubbleResponse().getName());
                    intent.putExtra("receiverAvt",lastMessageResponse.getBubbleResponse().getAvatar());
                    context.startActivity(intent);                }
            });
        }

    }
}
