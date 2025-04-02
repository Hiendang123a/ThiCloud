package com.example.app01.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.app01.DTO.Request.LoginRequest;
import com.example.app01.DTO.Response.APIResponse;
import com.example.app01.DTO.Response.AuthResponse;
import com.example.app01.R;
import com.example.app01.Api.AccountService;
import com.example.app01.Api.RetrofitClient;
import com.example.app01.Utils.TokenManager;
import com.example.app01.Validation.ValidationHelper;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText usernameET, passwordET;
    private Button signInBtn;
    private TextView signUpBtn, forgetPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        usernameET = findViewById(R.id.usernameET);
        passwordET = findViewById(R.id.passwordET);
        signUpBtn = findViewById(R.id.signUpBtn);
        forgetPassword = findViewById(R.id.forgotPasswordBtn);

        signUpBtn.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, RegisterActivity.class)));

        forgetPassword.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, ForgetPassword.class)));

        signInBtn = findViewById(R.id.signInBtn);
        signInBtn.setOnClickListener(v -> {
            String username = usernameET.getText().toString().trim();
            String password = passwordET.getText().toString().trim();
            List<String> errors = new ArrayList<>();

            if (ValidationHelper.isEmpty(username)) {
                errors.add(ValidationHelper.EMPTY_USERNAME);
            } else if (!ValidationHelper.isValidEmail(username)) {
                errors.add(ValidationHelper.INVALID_EMAIL);
            }

            if (ValidationHelper.isEmpty(password)) {
                errors.add(ValidationHelper.EMPTY_PASSWORD);
            }

            if (!errors.isEmpty()) {
                Toast.makeText(LoginActivity.this, String.join("\n", errors), Toast.LENGTH_LONG).show();
            } else {
                LoginRequest loginRequest = new LoginRequest(username,password);
                login(loginRequest);
            }
        });
    }
    private void login(LoginRequest loginRequest) {
        Retrofit retrofit = RetrofitClient.getRetrofitInstance(getApplicationContext());
        AccountService accountService = retrofit.create(AccountService.class);

        Call<APIResponse<AuthResponse>> call = accountService.login(loginRequest);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<APIResponse<AuthResponse>> call,@NonNull Response<APIResponse<AuthResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    APIResponse<AuthResponse> apiResponse = response.body();
                    Toast.makeText(LoginActivity.this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
                    Log.d("API Response", "UserID: " + apiResponse.getResult().getUserID());
                    TokenManager tokenManager = TokenManager.getInstance(getApplicationContext());
                    String accessToken = apiResponse.getResult().getAccessToken();
                    String refreshToken = apiResponse.getResult().getRefreshToken();
                    String userID = apiResponse.getResult().getUserID();
                    tokenManager.clearTokens();
                    tokenManager.saveTokens(accessToken,refreshToken,userID);
                    Log.e("UserID", userID);
                    startActivity(new Intent(LoginActivity.this, ChatActivity.class));
                } else {
                    try {
                        Gson gson = new Gson();
                        APIResponse<?> errorResponse = gson.fromJson(response.errorBody().string(), APIResponse.class);
                        Toast.makeText(LoginActivity.this, "Đăng nhập thất bại: " + errorResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Toast.makeText(LoginActivity.this, "Lỗi không xác định", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            @Override
            public void onFailure(@NonNull Call<APIResponse<AuthResponse>> call,@NonNull Throwable t) {
                Toast.makeText(LoginActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}