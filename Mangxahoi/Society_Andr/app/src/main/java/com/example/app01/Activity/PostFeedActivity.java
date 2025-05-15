package com.example.app01.Activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import java.io.IOException;

import com.example.app01.Api.PostService;
import com.example.app01.Api.RetrofitClient;
import com.example.app01.DTO.Request.CreatePostRequest;
import com.example.app01.DTO.Response.APIResponse;
import com.example.app01.Google.GoogleDriveHelper;
import com.example.app01.R;
import com.example.app01.Utils.TokenManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostFeedActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri uri;
    private ImageView selectedImage;
    private Button pickImageButton, postButton;
    private EditText captionEdit;
    private ImageButton backButton;
    private PostService postService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_post_feed);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        selectedImage = findViewById(R.id.selectedImage);
        pickImageButton = findViewById(R.id.pickImageButton);
        postButton = findViewById(R.id.postButton);
        captionEdit = findViewById(R.id.captionEdit);
        backButton = findViewById(R.id.backButton);

        // Nút trở về
        backButton.setOnClickListener(v -> finish());
        pickImageButton.setOnClickListener(v -> openImagePicker());
        postButton.setOnClickListener(v -> postCreate());
    }
    private void postCreate(){
        String caption = captionEdit.getText().toString();
        if (uri != null) {
            CreatePostRequest createPostRequest = new CreatePostRequest(TokenManager.getInstance(getApplicationContext()).getUserID(),caption,"");
            GoogleDriveHelper googleDriveHelper = new GoogleDriveHelper(PostFeedActivity.this);
            googleDriveHelper.uploadFileToSociety(uri, "image/jpg", () -> {
                if (googleDriveHelper.getFileID() != null) {
                    // Gán ID avatar sau khi upload thành công
                    createPostRequest.setImageUrl(googleDriveHelper.getFileID());
                    Log.d("ImageUpload", "File uploaded successfully, ID: " + googleDriveHelper.getFileID());
                    postService = RetrofitClient.getRetrofitInstance(getApplicationContext()).create(PostService.class);
                    postService.createPost(createPostRequest).enqueue(new Callback<>() {
                        @Override
                        public void onResponse(@NonNull Call<APIResponse<Void>> call, @NonNull Response<APIResponse<Void>> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                Toast.makeText(getApplicationContext(), "Đăng bài thành công", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                Toast.makeText(getApplicationContext(), "Đăng bài thất bại", Toast.LENGTH_SHORT).show();
                            }
                        }
                        @Override
                        public void onFailure(@NonNull Call<APIResponse<Void>> call, @NonNull Throwable t) {
                            Toast.makeText(getApplicationContext(), "Lỗi API", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Log.e("ImageUpload", "Upload failed, file ID is null.");
                }
            });
        }
        else {
            Toast.makeText(getApplicationContext(), "Vui lòng chọn ảnh", Toast.LENGTH_SHORT).show();
        }
    }
    private void openImagePicker() {
        @SuppressLint("IntentReset") Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            uri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                selectedImage.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}