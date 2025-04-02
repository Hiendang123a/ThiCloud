package com.example.app01.Activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.app01.DTO.Request.VerifyOTPRequest;
import com.example.app01.DTO.Response.APIResponse;
import com.example.app01.Google.GoogleDriveHelper;
import com.example.app01.R;
import com.example.app01.Api.AccountService;
import com.example.app01.Api.RetrofitClient;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class OTPVerifycation extends AppCompatActivity {
    private EditText otpET1, otpET2, otpET3, otpET4 , otpET5, otpET6;
    private EditText[] otpETs;

    private Button verifyBtn;
    private TextView resendBtn,gmailTV;
    private boolean resendEnable = false;
    private final int resendTime = 300;
    private int selecttedETPosition = 0;
    private boolean isDeleting = false; // Biến để theo dõi trạng thái xóa

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otpverifycation);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        String otp = getIntent().getStringExtra("otp");
        String username = getIntent().getStringExtra("username");
        String callType = getIntent().getStringExtra("CALL_TYPE");
        String avatarUriString = getIntent().getStringExtra("avatarUri");
        gmailTV = findViewById(R.id.gmailTV);
        gmailTV.setText(username);
        otpET1 = findViewById(R.id.otpET1);
        otpET2 = findViewById(R.id.otpET2);
        otpET3 = findViewById(R.id.otpET3);
        otpET4 = findViewById(R.id.otpET4);
        otpET5 = findViewById(R.id.otpET5);
        otpET6 = findViewById(R.id.otpET6);
        otpETs = new EditText[]{otpET1, otpET2, otpET3, otpET4, otpET5, otpET6};

        verifyBtn = findViewById(R.id.verifyBtn);
        resendBtn = findViewById(R.id.resendBtn);

        otpET1.addTextChangedListener(textWatcher);
        otpET2.addTextChangedListener(textWatcher);
        otpET3.addTextChangedListener(textWatcher);
        otpET4.addTextChangedListener(textWatcher);
        otpET5.addTextChangedListener(textWatcher);
        otpET6.addTextChangedListener(textWatcher);

        showKeyboard(otpET1);
        startCountDownTimer();
        resendBtn.setOnClickListener(v -> {
            if(resendEnable)
                startCountDownTimer();
        });

        verifyBtn.setOnClickListener(v ->{
            final String generateOTP = otpET1.getText().toString()+otpET2.getText().toString()+otpET3.getText().toString()+
                    otpET4.getText().toString()+otpET5.getText().toString()+otpET6.getText().toString();
            if(generateOTP.length()==6)
            {
                if(generateOTP.equals(otp))
                {
                    if ("fromRegister".equals(callType)) {
                        VerifyOTPRequest verifyOTPRequest = new VerifyOTPRequest(generateOTP,username);
                        createAccount(verifyOTPRequest,avatarUriString);
                    } else if ("fromForgetPass".equals(callType)) {
                        forgetPassword(otp,username);
                    }
                }
                else
                {
                    Toast.makeText(this, "OTP không chính xác!", Toast.LENGTH_SHORT).show();
                }
            }
            else
            {
                Toast.makeText(this, "Vui lòng nhập đủ 6 số!", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void createAccount(VerifyOTPRequest verifyOTPRequest, String avatarUriString) {
        GoogleDriveHelper googleDriveHelper = new GoogleDriveHelper(OTPVerifycation.this);

        if (avatarUriString != null) {
            Uri fileUri = Uri.parse(avatarUriString);
            googleDriveHelper.uploadFileToSociety(fileUri, "image/jpg", () -> {
                if (googleDriveHelper.getFileID() != null) {
                    // Gán ID avatar sau khi upload thành công
                    verifyOTPRequest.setIdAvatar(googleDriveHelper.getFileID());
                    Log.d("ImageUpload", "File uploaded successfully, ID: " + googleDriveHelper.getFileID());

                    // Gọi API sau khi đã có idAvatar
                    callApiToCreateAccount(verifyOTPRequest, googleDriveHelper);
                } else {
                    Log.e("ImageUpload", "Upload failed, file ID is null.");
                }
            });
        } else {
            // Nếu không có avatar, gọi API luôn
            callApiToCreateAccount(verifyOTPRequest, googleDriveHelper);
        }
    }
    private void callApiToCreateAccount(VerifyOTPRequest verifyOTPRequest, GoogleDriveHelper googleDriveHelper) {
        Retrofit retrofit = RetrofitClient.getRetrofitInstance(getApplicationContext());
        AccountService accountService = retrofit.create(AccountService.class);
        Call<APIResponse<Void>> call = accountService.verifyEmail(verifyOTPRequest);

        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<APIResponse<Void>> otp,@NonNull Response<APIResponse<Void>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    startActivity(new Intent(OTPVerifycation.this, MainActivity.class));
                    Toast.makeText(OTPVerifycation.this, "Đăng ký tài khoản thành công!", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        Gson gson = new Gson();
                        APIResponse<?> errorResponse = gson.fromJson(response.errorBody().string(), APIResponse.class);
                        Toast.makeText(OTPVerifycation.this, "Đăng ký tài khoản: " + errorResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Toast.makeText(OTPVerifycation.this, "Lỗi không xác định", Toast.LENGTH_SHORT).show();
                    } finally {
                        googleDriveHelper.deleteFileFromSociety(verifyOTPRequest.getIdAvatar());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<APIResponse<Void>> call,@NonNull Throwable t) {
                googleDriveHelper.deleteFileFromSociety(verifyOTPRequest.getIdAvatar());
                Toast.makeText(OTPVerifycation.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void forgetPassword(String otp, String username)
    {
        Intent intent = new Intent(OTPVerifycation.this, ChangePassword.class);
        intent.putExtra("otp", otp);
        intent.putExtra("username", username);
        startActivity(intent);
        Toast.makeText(OTPVerifycation.this, "Xác nhận OTP thành công!", Toast.LENGTH_SHORT).show();
    }
    private void startCountDownTimer(){
        resendEnable = false;
        resendBtn.setTextColor(Color.parseColor("#99000000"));

        new CountDownTimer(resendTime*1000,100){
            @Override
            public void onTick(long millisUntilFinished){
                int secondsRemaining = (int) millisUntilFinished / 1000;
                resendBtn.setText("Gửi lại mã sau (" + secondsRemaining + ")");
            }
            @Override
            public void onFinish(){
                resendEnable = true;
                resendBtn.setText("Gửi lại mã");
                resendBtn.setTextColor(getResources().getColor(R.color.primary));
            }

        }.start();
    }
    private void showKeyboard(EditText otpET)
    {
        otpET.requestFocus();
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.showSoftInput(otpET, InputMethodManager.SHOW_IMPLICIT);
    }
    private final TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
            isDeleting = (count > 0);
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void afterTextChanged(Editable editable) {
            if(editable.length()>0){
                switch (selecttedETPosition) {
                    case 4:
                        selecttedETPosition = 5;
                        showKeyboard(otpET6);
                        break;
                    case 3:
                        selecttedETPosition = 4;
                        showKeyboard(otpET5);

                        break;
                    case 2:
                        selecttedETPosition = 3;
                        showKeyboard(otpET4);

                        break;
                    case 1:
                        selecttedETPosition = 2;
                        showKeyboard(otpET3);

                        break;
                    case 0:
                        selecttedETPosition = 1;
                        showKeyboard(otpET2);
                        break;
                    default:
                        break;
                }
            }
        }
    };

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_DEL) {
            if (isDeleting) {
                otpETs[selecttedETPosition].setText("");
                isDeleting = false;
                Log.d("Text", "Xoa o co text");
            } else {
                if (selecttedETPosition > 0) {
                    selecttedETPosition--;
                    otpETs[selecttedETPosition].setText("");
                    Log.d("NoText", "Xoa o khong co text");
                    showKeyboard(otpETs[selecttedETPosition]);
                }
            }
            isDeleting = false;
            return true;
        } else {
            return super.onKeyUp(keyCode, event);
        }
    }
}