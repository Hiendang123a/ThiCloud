package com.example.app01.Adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.app01.DTO.Response.MessageResponse;
import com.example.app01.Google.GoogleDriveHelper;
import com.example.app01.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import lombok.Getter;
import lombok.Setter;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private static final int SEND = 0;
    private static final int RECEIVE = 1;
    private final Context mContext;
    private List<MessageResponse> messageResponseList;
    private final String receiveID;
    private final String receiveAvt;
    LayoutInflater layoutInflater;


    public MessageAdapter(Context context, List<MessageResponse> messageResponseList, String receiveID, String receiveAvt) {
        this.mContext = context;
        this.messageResponseList = messageResponseList;
        this.receiveID = receiveID;
        this.receiveAvt = receiveAvt;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType){
            case SEND:
                View itemView0 = layoutInflater.inflate(R.layout.item_chat_send,parent,false);
                return new SendViewHolder(itemView0);

            case RECEIVE:
                View itemView1 = layoutInflater.inflate(R.layout.item_chat_received,parent,false);
                return new ReceiveViewHolder(itemView1);
            default:
                throw new IllegalArgumentException("ViewType không hợp lệ: " + viewType);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MessageResponse messageResponse = messageResponseList.get(position);
        switch (getItemViewType(position)){
            case SEND:
                SendViewHolder sendViewHolder = (SendViewHolder) holder;

                if(messageResponse.isDeleted())
                {
                    sendViewHolder.tvMessageSent.setText("Tin nhắn đã bị xóa");
                    sendViewHolder.tvMessageSent.setTypeface(null, Typeface.ITALIC);
                    sendViewHolder.linearLayout.setVisibility(View.VISIBLE);
                    sendViewHolder.imgMessageSent.setVisibility(View.GONE);
                }
                else if(messageResponse.getContent()!= null && !messageResponse.getContent().isEmpty()) {
                    sendViewHolder.tvMessageSent.setText(messageResponse.getContent());
                    sendViewHolder.linearLayout.setVisibility(View.VISIBLE);
                    sendViewHolder.tvMessageSent.setTypeface(null, Typeface.NORMAL);
                    sendViewHolder.imgMessageSent.setVisibility(View.GONE);
                }
                else
                {
                    GoogleDriveHelper.loadImage(mContext,messageResponse.getImageUrl(),sendViewHolder.imgMessageSent);
                    sendViewHolder.imgMessageSent.setVisibility(View.VISIBLE);
                    sendViewHolder.linearLayout.setVisibility(View.GONE);

                }
                break;

            case RECEIVE:
                ReceiveViewHolder receiveViewHolder = (ReceiveViewHolder) holder;
                if (receiveAvt!= null && !receiveAvt.isEmpty()) {
                    GoogleDriveHelper.loadImage(mContext,receiveAvt,receiveViewHolder.imgUserAvatar);
                }

                if(messageResponse.isDeleted())
                {
                    receiveViewHolder.tvMessageReceived.setText("Tin nhắn đã bị xóa");
                    receiveViewHolder.tvMessageReceived.setTypeface(null, Typeface.ITALIC);
                    receiveViewHolder.linearLayout.setVisibility(View.VISIBLE);
                    receiveViewHolder.imgMessageReceived.setVisibility(View.GONE);
                }
                else if(messageResponse.getContent()!= null && !messageResponse.getContent().isEmpty()) {
                    receiveViewHolder.tvMessageReceived.setText(messageResponse.getContent());
                    receiveViewHolder.tvMessageReceived.setTypeface(null, Typeface.NORMAL); // Đảm bảo không bị nghiêng khi không cần
                    receiveViewHolder.linearLayout.setVisibility(View.VISIBLE);
                    receiveViewHolder.imgMessageReceived.setVisibility(View.GONE);
                }
                else
                {
                    GoogleDriveHelper.loadImage(mContext,messageResponse.getImageUrl(),receiveViewHolder.imgMessageReceived);
                    receiveViewHolder.imgMessageReceived.setVisibility(View.VISIBLE);
                    receiveViewHolder.linearLayout.setVisibility(View.GONE);
                }
                break;
            default:
                throw new IllegalArgumentException("ViewType không hợp lệ tại Bind: " + getItemViewType(position));
        }
    }

    @Override
    public int getItemCount() {
        return messageResponseList.size();
    }

    @Override
    public int getItemViewType(int position)
    {
        if(messageResponseList.get(position).getReceiverID().equals(receiveID))
            return SEND;
        else
            return RECEIVE;
    }

    @Setter
    @Getter
    public static class SendViewHolder extends RecyclerView.ViewHolder{
        private ImageView imgMessageSent;
        private TextView tvMessageSent;
        private LinearLayout linearLayout;

        public SendViewHolder(@NonNull View itemView) {
            super(itemView);
            this.imgMessageSent = itemView.findViewById(R.id.imgMessageSent);
            this.tvMessageSent = itemView.findViewById(R.id.tvMessageSent);
            this.linearLayout = itemView.findViewById(R.id.linearSend);
        }

    }

    @Setter
    @Getter
    public static class ReceiveViewHolder extends RecyclerView.ViewHolder{
        private CircleImageView imgUserAvatar;
        private ImageView imgMessageReceived;
        private TextView tvMessageReceived;
        private LinearLayout linearLayout;

        public ReceiveViewHolder(@NonNull View itemView) {
            super(itemView);
            this.imgMessageReceived = itemView.findViewById(R.id.imgMessageReceived);
            this.tvMessageReceived = itemView.findViewById(R.id.tvMessageReceived);
            this.imgUserAvatar = itemView.findViewById(R.id.imgUserAvatar);
            this.linearLayout = itemView.findViewById(R.id.linearReceive);
        }
    }
}
