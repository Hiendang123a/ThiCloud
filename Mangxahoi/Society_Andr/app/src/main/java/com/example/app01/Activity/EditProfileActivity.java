package com.example.app01.Activity;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.app01.Api.RetrofitClient;
import com.example.app01.Api.UserService;
import com.example.app01.DTO.Request.UpdateUserRequest;
import com.example.app01.DTO.Response.APIResponse;
import com.example.app01.DTO.Response.UserResponse;
import com.example.app01.Google.GoogleDriveHelper;
import com.example.app01.R;
import com.example.app01.Utils.TokenManager;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProfileActivity extends AppCompatActivity {
    private EditText nameEdit, dobEdit, bioEdit, phoneEdit;
    private ImageView avatar;
    private RadioButton genderMale, genderFemale;
    private Uri selectedImageUri;
    private UserService userService;
    private ImageView datePickerBtn;
    private DatePickerDialog datePickerDialog;
    private final Calendar calendar = Calendar.getInstance();

    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // Android 13+ (API 33+)
            if (checkSelfPermission(Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_MEDIA_IMAGES}, 1);
            }
        } else { // Android 12 trở xuống
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            }
        }
        nameEdit = findViewById(R.id.nameEdit);
        phoneEdit = findViewById(R.id.phoneEdit);
        dobEdit = findViewById(R.id.dateET);
        bioEdit = findViewById(R.id.bioEdit);
        genderMale = findViewById(R.id.genderMale);
        genderFemale = findViewById(R.id.genderFemale);
        avatar = findViewById(R.id.profileImage);

        datePickerBtn = findViewById(R.id.datePickerBtn);
        datePickerBtn.setOnClickListener(v ->
                showDatePicker()
        );

        TextView edit_picture = findViewById(R.id.editPicture);
        edit_picture.setOnClickListener(v -> openGallery());
        TextView btnDone = findViewById(R.id.btnDone);
        btnDone.setOnClickListener(v -> {
            String name = nameEdit.getText().toString();
            String phone = phoneEdit.getText().toString();
            String dobString = dobEdit.getText().toString();
            String bio = bioEdit.getText().toString();
            String gender = genderMale.isChecked() ? "Nam" : "Nữ";
            try{
                Date date = dateFormat.parse(dobString);
                UpdateUserRequest updateUserRequest = new UpdateUserRequest(name,gender,date,bio,phone,"");
                saveProfile(updateUserRequest);
            } catch (Exception e) {
                Log.e("Lỗi ngày sinh", "Lỗi khi parse ngày sinh", e);
            }
        });

        ImageView btnClose = findViewById(R.id.btnClose);
        btnClose.setOnClickListener(v-> finish());

        userService = RetrofitClient.getRetrofitInstance(getApplicationContext()).create(UserService.class);
        getUser(TokenManager.getInstance(getApplicationContext()).getUserID());
    }
    private void getUser(String userID) {
        userService.getUser(userID).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<APIResponse<UserResponse>> call, @NonNull Response<APIResponse<UserResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    APIResponse<UserResponse> apiResponse = response.body();
                    nameEdit.setText(apiResponse.getResult().getName());
                    phoneEdit.setText(apiResponse.getResult().getPhone());
                    bioEdit.setText(apiResponse.getResult().getBio());
                    Date dob = apiResponse.getResult().getDob();
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                    String formattedDate = sdf.format(dob);
                    dobEdit.setText(formattedDate);
                    if (apiResponse.getResult().getGender().equals("Nam") || apiResponse.getResult().getGender().equals("nam")) {
                        genderMale.setChecked(true);
                    } else {
                        genderFemale.setChecked(true);
                    }
                    if (apiResponse.getResult().getAvatar() != null && !apiResponse.getResult().getAvatar().isEmpty()) {
                        GoogleDriveHelper.loadImage(getApplicationContext(), apiResponse.getResult().getAvatar(), avatar);
                    } else {
                        avatar.setImageResource(R.drawable.default_avatar);
                    }
                } else {
                    Log.e("API_ERROR", "API trả về lỗi: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<APIResponse<UserResponse>> call, @NonNull Throwable t) {
                Log.e("API_ERROR", "Lỗi kết nối API: " + t.getMessage(), t);
                Toast.makeText(EditProfileActivity.this, "Lỗi kết nối API!" + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, 200);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 200 && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData(); // Lấy URI của ảnh
            Glide.with(this).load(selectedImageUri).into(avatar);
        }
    }
    private void saveProfile(UpdateUserRequest updateUserRequest) {
        GoogleDriveHelper googleDriveHelper = new GoogleDriveHelper(this);
        googleDriveHelper.uploadFileToSociety(selectedImageUri, "image/jpg", () -> {
            String fileId = googleDriveHelper.getFileID();
            if (fileId != null && !fileId.isEmpty()) {
                updateUserRequest.setAvatar(fileId);
                Log.d("ImageUpload", "File uploaded successfully, ID: " + fileId);
            } else {
                Log.w("ImageUpload", "File upload failed or returned null file ID");
            }
            callApiToUpdate(updateUserRequest);
        });
    }
    private void callApiToUpdate(UpdateUserRequest updateUserRequest) {
        userService.updateBasicInfo(updateUserRequest).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<APIResponse<UserResponse>> call, @NonNull Response<APIResponse<UserResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    APIResponse<UserResponse> apiResponse = response.body();
                    UserResponse updatedUser = apiResponse.getResult();
                    nameEdit.setText(apiResponse.getResult().getName());
                    phoneEdit.setText(apiResponse.getResult().getPhone());
                    bioEdit.setText(apiResponse.getResult().getBio());
                    Date dob = apiResponse.getResult().getDob();
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                    String formattedDate = sdf.format(dob);
                    dobEdit.setText(formattedDate);                    if (apiResponse.getResult().getGender().equals("Nam") || apiResponse.getResult().getGender().equals("nam")) {
                        genderMale.setChecked(true);
                    } else {
                        genderFemale.setChecked(true);
                    }
                    if (apiResponse.getResult().getAvatar() != null && !apiResponse.getResult().getAvatar().isEmpty()) {
                        GoogleDriveHelper.loadImage(getApplicationContext(), apiResponse.getResult().getAvatar(), avatar);
                    } else {
                        avatar.setImageResource(R.drawable.default_avatar);
                    }
                    Log.d("UpdateUser", "Cập nhật thành công: " + updatedUser.getName());
                } else {
                    Log.w("UpdateUser", "Response lỗi: " + response.code());
                }
            }
            @Override
            public void onFailure(@NonNull Call<APIResponse<UserResponse>> call, @NonNull Throwable t) {
                Log.e("UpdateUser", "onFailure: " + t.getMessage(), t);
            }
        });
    }
    private void showDatePicker() {
        if (datePickerDialog == null) {
            datePickerDialog = new DatePickerDialog(this,
                    (view, year, monthOfYear, dayOfMonth) -> {
                        Calendar selectedDate = Calendar.getInstance();
                        selectedDate.set(year, monthOfYear, dayOfMonth);
                        String formattedDate = dateFormat.format(selectedDate.getTime());
                        dobEdit.setText(formattedDate);
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            );
        }
        datePickerDialog.show();
    }
}