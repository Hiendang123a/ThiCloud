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

}
