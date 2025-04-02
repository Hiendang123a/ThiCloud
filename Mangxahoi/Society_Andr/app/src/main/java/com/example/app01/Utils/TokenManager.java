package com.example.app01.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.app01.Api.RetrofitClient;
import com.example.app01.Api.TokenService;
import com.example.app01.DTO.Request.RefreshTokenRequest;
import com.example.app01.DTO.Response.APIResponse;
import com.example.app01.DTO.Response.AuthResponse;
import com.google.gson.Gson;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

public class TokenManager {
    private static final String PREF_NAME = "app_prefs";
    private static final String KEY_ACCESS_TOKEN = "access_token";
    private static final String KEY_REFRESH_TOKEN = "refresh_token";
    private static final String KEY_USER_ID = "user_id";

    private static TokenManager instance;
    private final SharedPreferences sharedPreferences;
    private final SharedPreferences.Editor editor;
    private final Context appContext; // Thêm Context vào đây


    private TokenManager(Context context) {
        this.sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        this.editor = sharedPreferences.edit();
        this.appContext = context.getApplicationContext();
    }

    // Hàm để lấy instance duy nhất của TokenManager
    public static synchronized TokenManager getInstance(Context context) {
        if (instance == null) {
            instance = new TokenManager(context.getApplicationContext());
        }
        return instance;
    }

    public void saveTokens(String accessToken, String refreshToken, String userID) {
        editor.putString(KEY_ACCESS_TOKEN, accessToken);
        editor.putString(KEY_REFRESH_TOKEN, refreshToken);
        editor.putString(KEY_USER_ID, userID);
        editor.apply();
    }

    public String getAccessToken() {
        return sharedPreferences.getString(KEY_ACCESS_TOKEN, null);
    }

    public String getRefreshToken() {
        return sharedPreferences.getString(KEY_REFRESH_TOKEN, null);
    }

    public String getUserID() {
        return sharedPreferences.getString(KEY_USER_ID, null);
    }

    public void clearTokens() {
        editor.remove(KEY_ACCESS_TOKEN);
        editor.remove(KEY_REFRESH_TOKEN);
        editor.remove(KEY_USER_ID);
        editor.apply();
    }

    public void refreshToken(Runnable onComplete) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            try {
                if (getRefreshToken() == null) {
                    Log.e("TokenManager", "Token null");
                    clearTokens();
                    onComplete.run();
                }

                Retrofit retrofit = RetrofitClient.getRetrofitInstance(appContext);
                TokenService tokenService = retrofit.create(TokenService.class);
                Call<APIResponse<AuthResponse>> call = tokenService.refreshToken(new RefreshTokenRequest(getRefreshToken()));
                Response<APIResponse<AuthResponse>> response = call.execute();

                if (response.isSuccessful() && response.body() != null) {
                    APIResponse<AuthResponse> apiResponse = response.body();
                    saveTokens(apiResponse.getResult().getAccessToken(), apiResponse.getResult().getRefreshToken(), getUserID());
                    Log.d("TokenManager", "Làm mới token thành công!");

                    if (onComplete != null) {
                        onComplete.run();  // Chỉ gọi lại API nếu làm mới token thành công
                    }
                } else {
                    Log.e("TokenManager", "Lỗi API: " + response.code());
                    clearTokens();
                }
            } catch (Exception e) {
                Log.e("TokenManager", "Refresh thất bại: ", e);
                clearTokens();
            } finally {
                executor.shutdown();
            }
        });
    }

    public void handleErrorResponse(Response<?> response, Runnable retryAction) {
        try {
            if (response.errorBody() == null) {
                Log.e("TokenManager", "Lỗi không xác định: errorBody null");
                return;
            }

            Gson gson = new Gson();
            APIResponse<?> errorResponse = gson.fromJson(response.errorBody().string(), APIResponse.class);

            Log.e("TokenManager", "Lỗi: ");

            if ("UNAUTHORIZED".equals(errorResponse.getHttpStatus())) {
                refreshToken(retryAction);
            }
        } catch (Exception e) {
            Log.e("TokenManager", "Lỗi không xác định", e);
        }
    }
}
