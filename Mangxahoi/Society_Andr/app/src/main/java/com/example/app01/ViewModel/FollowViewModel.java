package com.example.app01.ViewModel;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.app01.Api.FollowService;
import com.example.app01.Api.RetrofitClient;
import com.example.app01.Api.UserService;
import com.example.app01.DTO.Response.APIResponse;
import com.example.app01.DTO.Response.BubbleResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FollowViewModel extends AndroidViewModel {
    private final FollowService followService;
    private final UserService userService;

    private MutableLiveData<List<BubbleResponse>> followers = new MutableLiveData<>();
    private MutableLiveData<List<BubbleResponse>> following = new MutableLiveData<>();
    private MutableLiveData<List<BubbleResponse>> searchResults = new MutableLiveData<>();
    public FollowViewModel(@NonNull Application application) {
        super(application);
        Context context = application.getApplicationContext();
        followService = RetrofitClient.getRetrofitInstance(context).create(FollowService.class);
        userService = RetrofitClient.getRetrofitInstance(context).create(UserService.class);
    }

    public LiveData<List<BubbleResponse>> getFollowers() {
        loadFollowers();
        return followers;
    }

    public LiveData<List<BubbleResponse>> getFollowing() {
        loadFollowing();
        return following;
    }
    public LiveData<List<BubbleResponse>> getSearchResults() {
        return searchResults;
    }


    public void loadFollowers() {
        followService.getFollowers().enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<APIResponse<List<BubbleResponse>>> call, Response<APIResponse<List<BubbleResponse>>> response) {
                if (response.isSuccessful()) {
                    APIResponse<List<BubbleResponse>> apiResponse = response.body();
                    assert apiResponse != null;
                    List<BubbleResponse> follower = apiResponse.getResult();
                    followers.postValue(follower);
                }
            }
            @Override
            public void onFailure(Call<APIResponse<List<BubbleResponse>>> call, Throwable t) {
                followers.postValue(null);
            }
        });
    }

    public void loadFollowing() {
        followService.getFollowing().enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<APIResponse<List<BubbleResponse>>> call, Response<APIResponse<List<BubbleResponse>>> response) {
                if (response.isSuccessful()) {
                    APIResponse<List<BubbleResponse>> apiResponse = response.body();
                    assert apiResponse != null;
                    List<BubbleResponse> followings = apiResponse.getResult();
                    following.postValue(followings);
                }
            }

            @Override
            public void onFailure(Call<APIResponse<List<BubbleResponse>>> call, Throwable t) {
                following.postValue(null);
            }
        });
    }

    //search
    public void searchUsers(String query) {
        userService.searchUsers(query, 0, 10).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<APIResponse<List<BubbleResponse>>> call, Response<APIResponse<List<BubbleResponse>>> response) {
                if (response.isSuccessful()) {
                    APIResponse<List<BubbleResponse>> apiResponse = response.body();
                    assert apiResponse != null;
                    List<BubbleResponse> followings = apiResponse.getResult();
                    searchResults.postValue(followings);
                }
            }
            @Override
            public void onFailure(Call<APIResponse<List<BubbleResponse>>> call, Throwable t) {
                searchResults.postValue(null);
            }
        });
    }

}
