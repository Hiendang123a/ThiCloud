package com.example.app01.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.app01.Google.GoogleDriveHelper;
import com.example.app01.R;

import java.util.ArrayList;
import java.util.List;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ImageViewHolder> {

    private Context context;
    private List<String> imageUrls;
    public GalleryAdapter(Context context, List<String> imageUrls) {
        this.context = context;
        this.imageUrls = imageUrls != null ? imageUrls : new ArrayList<>();
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_gallery, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        String imageUrl = imageUrls.get(position);
        if(imageUrl != null && !imageUrl.isEmpty()){
            GoogleDriveHelper.loadImage(context,imageUrl,holder.imageView);
        }
    }

    @Override
    public int getItemCount() {
        return imageUrls.size();
    }

    // ViewHolder cho ảnh
    public static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
        }
    }

    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = (imageUrls != null) ? imageUrls : new ArrayList<>();
        notifyDataSetChanged();
    }
}
