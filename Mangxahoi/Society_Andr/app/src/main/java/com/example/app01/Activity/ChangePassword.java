package com.example.app01.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.example.app01.DTO.Request.VerifyOTPRepassRequest;
import com.example.app01.DTO.Response.APIResponse;
import com.example.app01.R;
import com.example.app01.Api.AccountService;
import com.example.app01.Api.RetrofitClient;
import com.example.app01.Validation.ValidationHelper;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ChangePassword extends AppCompatActivity {
    private TextView gmailTV;
    private EditText passwordET, cfpasswordET;
    private Button changePassBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        gmailTV = findViewById(R.id.gmailTV);
        passwordET = findViewById(R.id.passwordET);
        cfpasswordET = findViewById(R.id.cfpasswordET);
        changePassBtn = findViewById(R.id.changePassBtn);

        String otp = getIntent().getStringExtra("otp");
        String username = getIntent().getStringExtra("username");

        gmailTV.setText(username);

        changePassBtn.setOnClickListener(v -> {
            String password = passwordET.getText().toString();
            String cfpassword = cfpasswordET.getText().toString();
            if(checkPassword(password,cfpassword))
            {
                VerifyOTPRepassRequest verifyOTPRepassRequest = new VerifyOTPRepassRequest(otp, username, password);
                changePassword(verifyOTPRepassRequest);
            }
        });
    }
    private void changePassword(VerifyOTPRepassRequest verifyOTPRepassRequest)
    {
        Retrofit retrofit = RetrofitClient.getRetrofitInstance(getApplicationContext());
        AccountService accountService = retrofit.create(AccountService.class);
        Call<APIResponse<Void>> call = accountService.verifyEmailRepass(verifyOTPRepassRequest);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<APIResponse<Void>> call,@NonNull Response<APIResponse<Void>> response) {
                if (response.isSuccessful()) {
                    startActivity(new Intent(ChangePassword.this, MainActivity.class));
                    Toast.makeText(ChangePassword.this, "Đặt lại mật khẩu thành công!", Toast.LENGTH_SHORT).show();
                } else try {
                    Gson gson = new Gson();
                    APIResponse<?> errorResponse = gson.fromJson(response.errorBody().string(), APIResponse.class);
                    Toast.makeText(ChangePassword.this, "Đặt lại mật khẩu thất bại: " + errorResponse.getMessage(), Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Toast.makeText(ChangePassword.this, "Lỗi không xác định", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(@NonNull Call<APIResponse<Void>> call,@NonNull Throwable t) {
                Toast.makeText(ChangePassword.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("API Error", "Error message: " + t.getMessage(), t);
            }
        });
    }
    private boolean checkPassword(String password, String cfpassword){
        if(password.equals(cfpassword))
        {
            List<String> errors = new ArrayList<>();
            if (ValidationHelper.isEmpty(password)) {
                errors.add(ValidationHelper.EMPTY_PASSWORD);
            }else if (!ValidationHelper.isValidPassword(password))
            {
                errors.add(ValidationHelper.INVALID_PASSWORD);
            }
            if (!errors.isEmpty()) {
                Toast.makeText(ChangePassword.this, String.join("\n", errors), Toast.LENGTH_LONG).show();
                return false;
            } else {
                return true;
            }
        }
        else
        {
            Toast.makeText(this, "Mật khẩu không trùng khớp", Toast.LENGTH_SHORT).show();
            return false;
        }
    }
}