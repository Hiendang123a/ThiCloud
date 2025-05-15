package com.example.app01.Activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.app01.Api.RetrofitClient;
import com.example.app01.Api.UserService;
import com.example.app01.R;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RePassActivity extends AppCompatActivity {

    private EditText edtCurrentPassword, edtNewPassword, edtRetypeNewPassword;
    private TextView txt_username;
    private Button btnChangePassword;
    private UserService userService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_re_pass);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.repassword), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        //anh xa
        edtCurrentPassword = findViewById(R.id.oldPwd);
        edtNewPassword = findViewById(R.id.newPwd);
        edtRetypeNewPassword = findViewById(R.id.reNewPwd);
        txt_username = findViewById(R.id.txtUsername);
        btnChangePassword = findViewById(R.id.btn);
        //
        //txt_username.setText(username);
        //
        userService = RetrofitClient.getRetrofitInstance(getApplicationContext()).create(UserService.class);
        //
        btnChangePassword.setOnClickListener(v -> {
            changePassword();
        });

    }
    private void changePassword() {
        // Lấy dữ liệu từ các EditText
        String currentPassword = edtCurrentPassword.getText().toString().trim();
        String newPassword = edtNewPassword.getText().toString().trim();
        String retypePassword = edtRetypeNewPassword.getText().toString().trim();

        // Kiểm tra các trường nhập liệu
        if (currentPassword.isEmpty() || newPassword.isEmpty() || retypePassword.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
            return;
        }
        // Kiểm tra mật khẩu mới và mật khẩu nhập lại có trùng nhau không
        if (!newPassword.equals(retypePassword)) {
            Toast.makeText(this, "Mật khẩu mới không khớp!", Toast.LENGTH_SHORT).show();
            return;
        }
        // Tạo Map để chứa dữ liệu cần gửi
        Map<String, String> request = new HashMap<>();
        request.put("currentPassword", currentPassword);
        request.put("newPassword", newPassword);
        /*
        // Gọi API đổi mật khẩu với Map
        Call<ResponseBody> call = userService.changePassword(request);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        String responseData = response.body().string();
                        Toast.makeText(RePassActivity.this, "Đổi mật khẩu thành công: " + responseData, Toast.LENGTH_SHORT).show();
                        finish();
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(RePassActivity.this, "Lỗi xử lý dữ liệu trả về!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(RePassActivity.this, "Đổi mật khẩu thất bại!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(RePassActivity.this, "Lỗi kết nối!", Toast.LENGTH_SHORT).show();
            }
        });

         */
    }
}