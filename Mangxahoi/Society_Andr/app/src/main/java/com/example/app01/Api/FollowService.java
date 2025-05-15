package com.example.app01.Api;

import com.example.app01.DTO.Response.APIResponse;
import com.example.app01.DTO.Response.BubbleResponse;
import com.example.app01.DTO.Response.FollowResponse;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface FollowService {
    @GET("/api/follow/following")
    Call<APIResponse<List<BubbleResponse>>> getFollowing();
    @GET("/api/follow/followers")
    Call<APIResponse<List<BubbleResponse>>> getFollowers();
    @DELETE("/api/follow/unfollow")
    Call<APIResponse<FollowResponse>> unfollow(@Query("receiverID") String receiverID);
    @DELETE("/api/follow/remove")
    Call<APIResponse<FollowResponse>> removeFollower(@Query("receiverID") String receiverID);
    @GET("/api/follow/requests")
    Call<APIResponse<List<BubbleResponse>>> getFollowRequest();
    @DELETE("/api/follow/reject")
    Call<APIResponse<FollowResponse>> rejectFollowRequest(@Query("senderID") String senderID);
    @POST("/api/follow/accept")
    Call<APIResponse<FollowResponse>> acceptFollowRequest(@Query("senderID") String senderID);

    //is following?
    @GET("/api/follow/is-following/{receiverID}")
    Call<APIResponse<Boolean>> isFollowing(@Path("receiverID") String receiverID);

    //check status
    @GET("/api/follow/status")
    Call<APIResponse<Boolean>> checkFollowRequestStatus(@Query("receiverID") String receiverID);

    // ✅ Lấy số lượng followers
    @GET("/api/follow/followers/count")
    Call<APIResponse<Integer>> getFollowersCount(@Query("userID") String userID);

    // ✅ Lấy số lượng following
    @GET("/api/follow/following/count")
    Call<APIResponse<Integer>> getFollowingCount(@Query("userID") String userID);

    // ✅ Gửi follow request (hoặc tự động follow nếu account public)
    @POST("/api/follow/request")
    Call<APIResponse<FollowResponse>> sendFollowRequest(@Query("receiverID") String receiverID);
}
