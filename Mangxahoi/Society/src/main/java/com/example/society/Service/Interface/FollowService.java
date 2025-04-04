package com.example.society.Service.Interface;

import com.example.society.DTO.Response.BubbleResponse;
import com.example.society.DTO.Response.FollowResponse;
import org.bson.types.ObjectId;

import java.util.List;

public interface FollowService {
    FollowResponse sendFollowRequest(ObjectId senderId, ObjectId receiverId);
    FollowResponse acceptFollowRequest(ObjectId senderId, ObjectId receiverId);
    FollowResponse rejectFollowRequest(ObjectId senderId, ObjectId receiverId);
    FollowResponse unfollow(ObjectId senderId, ObjectId receiverId);
    FollowResponse cancelFollowRequest(ObjectId senderId, ObjectId receiverId);
    List<BubbleResponse> getFollowing(ObjectId userId);
    List<BubbleResponse> getFollowers(ObjectId userId);
    List<BubbleResponse> getFollowRequests(ObjectId userId);
    int getFollowersCount(ObjectId userId);
    int getFollowingCount(ObjectId userId);
    boolean isFollowing(ObjectId senderId, ObjectId receiverId);
    String removeFollower(ObjectId senderId, ObjectId receiverId);
    boolean hasPendingRequest(ObjectId senderId, ObjectId receiverId);

}
