package com.example.app01.Activity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app01.Adapter.SearchAdapter;
import com.example.app01.DTO.Response.BubbleResponse;
import com.example.app01.R;
import com.example.app01.ViewModel.FollowViewModel;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {
    private SearchView searchView;
    private RecyclerView recyclerView;
    private SearchAdapter adapter;
    private FollowViewModel followViewModel;
    private final List<BubbleResponse> userList = new ArrayList<>();
    private final Handler searchHandler = new Handler();
    private Runnable searchRunnable;
    private View loadingIndicator;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_search);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        // Ánh xạ
        searchView = findViewById(R.id.searchView);
        recyclerView = findViewById(R.id.recyclerView);
        loadingIndicator = findViewById(R.id.loadingIndicator);
        //
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SearchAdapter(this, userList);
        recyclerView.setAdapter(adapter);

        followViewModel = new ViewModelProvider(this).get(FollowViewModel.class);

        followViewModel.getSearchResults().observe(this, follows -> {
            loadingIndicator.setVisibility(View.GONE);

            if (follows != null) {
                userList.clear();
                userList.addAll(follows);
                adapter.notifyDataSetChanged();

                if (follows.isEmpty()) {
                    recyclerView.setVisibility(View.GONE);
                    Toast.makeText(SearchActivity.this, "Không tìm thấy kết quả!", Toast.LENGTH_SHORT).show();
                } else {
                    recyclerView.setVisibility(View.VISIBLE);
                }
            } else {
                Toast.makeText(SearchActivity.this, "Lỗi tải dữ liệu!", Toast.LENGTH_SHORT).show();
            }
        });

        // Xử lý tìm kiếm
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                hideKeyboard();
                searchWithDebounce(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchWithDebounce(newText);
                return true;
            }
        });

        // Mở bàn phím khi click vào ô tìm kiếm
        searchView.setOnQueryTextFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                showKeyboard();
            }
        });
    }

    // Tìm kiếm có độ trễ để tránh spam API
    private void searchWithDebounce(String query) {
        if (searchRunnable != null) {
            searchHandler.removeCallbacks(searchRunnable);
        }
        //
        if (query.trim().isEmpty()) {
            loadingIndicator.setVisibility(View.GONE);
            recyclerView.setVisibility(View.GONE);
            return;
        }
        //
        recyclerView.setVisibility(View.GONE);

        loadingIndicator.setVisibility(View.VISIBLE);
        loadingIndicator.setAlpha(0f);
        loadingIndicator.animate().alpha(1f).setDuration(300).start();

        searchRunnable = () -> followViewModel.searchUsers(query);
        searchHandler.postDelayed(searchRunnable, 500); // Delay 500ms
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
        }
    }

    private void showKeyboard() {
        searchView.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.showSoftInput(searchView, InputMethodManager.SHOW_IMPLICIT);
        }
    }
}