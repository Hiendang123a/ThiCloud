package com.example.app01.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.app01.R;
import com.example.app01.Utils.TokenManager;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ImageView btn_messenger = findViewById(R.id.btn_messenger);
        btn_messenger.setOnClickListener(v -> checkTokenAndProceed());
    }

    private void checkTokenAndProceed() {
        TokenManager tokenManager = TokenManager.getInstance(getApplicationContext());

        tokenManager.refreshToken(() -> runOnUiThread(() -> {
            if (tokenManager.getAccessToken() == null) {
                Log.d("TokenManager", "Token is null after refresh, redirecting to LoginActivity...");
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            } else {
                Log.d("TokenManager", "Token exists after refresh, opening activity: " + ChatActivity.class.getSimpleName());
                Intent intent = new Intent(MainActivity.this, ChatActivity.class);
                startActivity(intent);
            }
        }));
    }
}