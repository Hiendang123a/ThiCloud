package com.example.app01.Api;

import android.content.Context;
import com.example.app01.Utils.AuthInterceptor;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static final String BASE_URL = "http://10.0.2.2:8080/";
    //private static final String BASE_URL = "http://192.168.28.175:8080/";

    private static Retrofit retrofit = null;

    public static Retrofit getRetrofitInstance(Context context) {
        if (retrofit == null) {
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(new AuthInterceptor(context)) // Thêm AuthInterceptor vào OkHttpClient
                    .connectTimeout(30, TimeUnit.SECONDS)  // Timeout kết nối
                    .readTimeout(30, TimeUnit.SECONDS)     // Timeout đọc
                    .writeTimeout(30, TimeUnit.SECONDS)    // Timeout ghi
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
