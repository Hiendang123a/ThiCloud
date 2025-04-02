package com.example.app01.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.example.app01.DTO.Response.APIResponse;
import com.example.app01.DTO.Response.OTPResponse;
import com.example.app01.R;
import com.example.app01.Api.AccountService;
import com.example.app01.Api.RetrofitClient;
import com.example.app01.Validation.ValidationHelper;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ForgetPassword extends AppCompatActivity {
    private EditText usernameET;
    private Button getOTPBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        usernameET = findViewById(R.id.usernameET);
        getOTPBtn = findViewById(R.id.getOTPBtn);
        getOTPBtn.setOnClickListener(v ->{
            String username = usernameET.getText().toString();
            List<String> errors = new ArrayList<>();

            if (ValidationHelper.isEmpty(username)) {
                errors.add(ValidationHelper.EMPTY_USERNAME);
            } else if (!ValidationHelper.isValidEmail(username)) {
                errors.add(ValidationHelper.INVALID_EMAIL);
            }

            if (!errors.isEmpty()) {
                Toast.makeText(ForgetPassword.this, String.join("\n", errors), Toast.LENGTH_LONG).show();
            } else {
                getOTP(username);
            }
        });
    }
    private void getOTP(String username)
    {
        Retrofit retrofit = RetrofitClient.getRetrofitInstance(getApplicationContext());
        AccountService accountService = retrofit.create(AccountService.class);
        Map<String, String> request = new HashMap<>();
        request.put("username", username);
        Call<APIResponse<OTPResponse>> call = accountService.forgetPass(request);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<APIResponse<OTPResponse>> call,@NonNull Response<APIResponse<OTPResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    APIResponse<OTPResponse> apiResponse = response.body();
                    Intent intent = new Intent(ForgetPassword.this, OTPVerifycation.class);

                    intent.putExtra("otp", apiResponse.getResult().getOtp());
                    intent.putExtra("username",apiResponse.getResult().getUsername());
                    intent.putExtra("CALL_TYPE", "fromForgetPass");
                    Log.d("API Response", "OTP: " + apiResponse.getResult().getOtp());
                    startActivity(intent);
                } else {
                    try {
                        Gson gson = new Gson();
                        APIResponse<?> errorResponse = gson.fromJson(response.errorBody().string(), APIResponse.class);
                        Toast.makeText(ForgetPassword.this, "Đăng nhập thất bại: " + errorResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Toast.makeText(ForgetPassword.this, "Lỗi không xác định", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            @Override
            public void onFailure(@NonNull Call<APIResponse<OTPResponse>> call,@NonNull Throwable t) {
                Toast.makeText(ForgetPassword.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}