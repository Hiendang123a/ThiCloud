package com.example.app01.Utils;

import android.content.Context;
import android.util.Log;

import org.jetbrains.annotations.NotNull;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import java.io.IOException;

public class AuthInterceptor implements Interceptor {
    private final TokenManager tokenManager;

    public AuthInterceptor(Context context) {
        this.tokenManager = TokenManager.getInstance(context);
    }

    @NotNull
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();
        String url = originalRequest.url().toString(); // Lấy URL của request

        // Bỏ qua token cho API `/api/account/**` và `/api/token/refresh`
        if (url.contains("/api/account/") || url.contains("/api/token/refresh")) {
            Log.d("intercept", "khong gui token");
            return chain.proceed(originalRequest);
        }

        // Thêm token vào tất cả các API khác
        String token = tokenManager.getAccessToken();
        Request.Builder requestBuilder = originalRequest.newBuilder();

        if (token != null) {
            Log.d("intercept", "da gui token");
            requestBuilder.header("Authorization", "Bearer " + token);
        }
        Request modifiedRequest = requestBuilder.build();
        return chain.proceed(modifiedRequest);
    }
}
