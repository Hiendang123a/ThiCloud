package com.example.app01.Activity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.app01.DTO.Request.AccountDTO;
import com.example.app01.DTO.Request.RegisterUserRequest;
import com.example.app01.DTO.Request.UserDTO;
import com.example.app01.DTO.Response.APIResponse;
import com.example.app01.DTO.Response.OTPResponse;

import com.example.app01.R;
import com.example.app01.Api.AccountService;
import com.example.app01.Api.RetrofitClient;
import com.example.app01.Validation.ValidationHelper;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class RegisterActivity extends AppCompatActivity {
    private ActivityResultLauncher<Intent> pickImageLauncher;
    private Uri selectedImageUri;

    private TextView signInBtn;
    private ImageView datePickerBtn,uploadIcon,avatarImageView;
    private final Calendar calendar = Calendar.getInstance();
    private Button signUpBtn;
    private RadioGroup genderRadioGroup;
    private RadioButton maleRdb;
    private EditText dateET,fullnameET, usernameET, passwordET,
            phoneET;
    private DatePickerDialog datePickerDialog;


    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Mapping();

        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        selectedImageUri = result.getData().getData();
                        if (selectedImageUri != null) {
                            // Hiển thị ảnh lên ImageView
                            Glide.with(this)
                                    .load(selectedImageUri)
                                    .into(avatarImageView);
                        }
                    }
                });

        datePickerBtn.setOnClickListener(v ->
                showDatePicker()
        );

        signInBtn.setOnClickListener(v->
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class))
        );

        uploadIcon.setOnClickListener(v-> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            pickImageLauncher.launch(Intent.createChooser(intent, "Chọn ảnh"));
        });

        signUpBtn.setOnClickListener(v->{
            String fullname = fullnameET.getText().toString();
            String username = usernameET.getText().toString();
            String password = passwordET.getText().toString();
            String phone = phoneET.getText().toString();


            int selectedId = genderRadioGroup.getCheckedRadioButtonId();
            String gender = ((RadioButton) findViewById(selectedId)).getText().toString();
            String dobString = dateET.getText().toString();
            try {
                Date dob = dateFormat.parse(dobString);
                UserDTO userDTO = new UserDTO(fullname,gender,dob,address,hometown,"");
                AccountDTO accountDTO = new AccountDTO(username,password);
                RegisterUserRequest registerUserRequest = new RegisterUserRequest(userDTO,accountDTO);

                List<String> errors = new ArrayList<>();

                if (ValidationHelper.isEmpty(username)) {
                    errors.add(ValidationHelper.EMPTY_USERNAME);
                } else if (!ValidationHelper.isValidEmail(username)) {
                    errors.add(ValidationHelper.INVALID_EMAIL);
                }

                if (ValidationHelper.isEmpty(password)) {
                    errors.add(ValidationHelper.EMPTY_PASSWORD);
                }else if (!ValidationHelper.isValidPassword(password))
                {
                    errors.add(ValidationHelper.INVALID_PASSWORD);
                }
                if (ValidationHelper.isEmpty(fullname))
                    errors.add(ValidationHelper.EMPTY_FULLNAME);

                if (ValidationHelper.isEmpty(address))
                    errors.add(ValidationHelper.EMPTY_ADDRESS);

                if (ValidationHelper.isEmpty(hometown))
                    errors.add(ValidationHelper.EMPTY_HOMETOWN);

                if(!ValidationHelper.isValidDob(dob))
                    errors.add(ValidationHelper.UNDER_AGE);

                if (!errors.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, String.join("\n", errors), Toast.LENGTH_LONG).show();
                } else {
                    signUp(registerUserRequest);
                }
            } catch (Exception e) {
                Log.e("SignUpError", "Lỗi khi parse ngày sinh", e);
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
                        dateET.setText(formattedDate);
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            );
        }
        datePickerDialog.show();
    }

    private void signUp(RegisterUserRequest registerUserRequest){
        Retrofit retrofit = RetrofitClient.getRetrofitInstance(getApplicationContext());
        AccountService accountService = retrofit.create(AccountService.class);
        Call<APIResponse<OTPResponse>> call = accountService.register(registerUserRequest);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<APIResponse<OTPResponse>> call,@NonNull Response<APIResponse<OTPResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    APIResponse<OTPResponse> apiResponse = response.body();
                    Intent intent = new Intent(RegisterActivity.this, OTPVerifycation.class);

                    intent.putExtra("otp", apiResponse.getResult().getOtp());
                    intent.putExtra("username",apiResponse.getResult().getUsername());
                    intent.putExtra("CALL_TYPE", "fromRegister");
                    if (selectedImageUri != null) {
                        intent.putExtra("avatarUri", selectedImageUri.toString());
                    }
                    startActivity(intent);
                    Log.d("API Response", "OTP: " + apiResponse.getResult().getOtp());  // OTP sẽ được hiển thị
                } else {
                    try {
                        Gson gson = new Gson();
                        APIResponse<?> errorResponse = null;
                        if (response.errorBody() != null) {
                            errorResponse = gson.fromJson(response.errorBody().string(), APIResponse.class);
                        }
                        if (errorResponse != null) {
                            Toast.makeText(RegisterActivity.this, "Đăng ký tài khoản thất bại: " + errorResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(RegisterActivity.this, "Lỗi không xác định", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            @Override
            public void onFailure(@NonNull Call<APIResponse<OTPResponse>> call,@NonNull Throwable t) {
                Toast.makeText(RegisterActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void Mapping(){
        fullnameET = findViewById(R.id.fullnameET);
        usernameET = findViewById(R.id.usernameET);
        passwordET = findViewById(R.id.passwordET);
        phoneET = findViewById(R.id.phoneET);
        uploadIcon = findViewById(R.id.uploadIcon);
        avatarImageView = findViewById(R.id.avatarImageView);
        dateET = findViewById(R.id.dateET);
        genderRadioGroup = findViewById(R.id.genderRadioGroup);
        maleRdb = findViewById(R.id.maleRdb);
        datePickerBtn = findViewById(R.id.datePickerBtn);
        signInBtn = findViewById(R.id.signInBtn);
        signUpBtn = findViewById(R.id.signUpBtn);

        maleRdb.setChecked(true);
        String currentDate = dateFormat.format(calendar.getTime());
        dateET.setText(currentDate);
    }
}