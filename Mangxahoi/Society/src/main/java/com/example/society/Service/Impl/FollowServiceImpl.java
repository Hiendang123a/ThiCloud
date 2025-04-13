package com.example.society.Service.Impl;

import com.example.society.DTO.Response.BubbleResponse;
import com.example.society.DTO.Response.FollowResponse;
import com.example.society.Entity.Account;
import com.example.society.Entity.Follow;
import com.example.society.Entity.FollowAction;
import com.example.society.Entity.User;
import com.example.society.Repository.IAccountRepository;
import com.example.society.Repository.IFollowRepository;
import com.example.society.Repository.IUserRepository;
import com.example.society.Service.Interface.FollowService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class FollowServiceImpl implements FollowService {
    @Autowired
    IFollowRepository followRepository;

    @Autowired
    IUserRepository userRepository;

    @Autowired
    IAccountRepository accountRepository;

    @Override
    public FollowResponse sendFollowRequest(ObjectId senderId, ObjectId receiverId) {
        FollowResponse followResponse = new FollowResponse();
        Optional<Account> accountReceiver = accountRepository.findByUserID(receiverId);
        if (accountReceiver.isEmpty()) {
            followResponse.setMessage("Account is not exist");
            return followResponse;
        }

        boolean receiverIsPrivate = accountReceiver.get().getIsPrivate();

        Optional<Follow> existingFollow = followRepository.findByUser1AndUser2(senderId, receiverId);
        if (existingFollow.isPresent()) {
            followResponse.setMessage("Follow request already sent!");
            return followResponse;
        }

        FollowAction action = receiverIsPrivate ? FollowAction.REQUEST : FollowAction.ACCEPT;

        Follow follow = new Follow();
        follow.setUser1(senderId);
        follow.setUser2(receiverId);
        follow.setStatus(action);
        followRepository.save(follow);

        if (receiverIsPrivate) {
            followResponse.setMessage("Follow request sent!");
        } else {
            Optional<User> user = userRepository.findUserByUserID(receiverId);
            if (user.isPresent())
                followResponse.setMessage("You are now following " + user.get().getName());
            else
                followResponse.setMessage("You are now following");
        }
        return followResponse;
    }


    @Override
    public FollowResponse acceptFollowRequest(ObjectId senderId, ObjectId receiverId) {
        FollowResponse followResponse = new FollowResponse();
        Optional<Follow> follow = followRepository.findByUser1AndUser2AndStatus(senderId, receiverId,FollowAction.REQUEST);
        if (follow.isEmpty()) {
            followResponse.setMessage("No pending follow request!");
            return followResponse;
        }
        follow.get().setStatus(FollowAction.ACCEPT);
        followRepository.save(follow.get());
        followResponse.setMessage("Follow request accepted!");
        return followResponse;
    }

    @Override
    public FollowResponse rejectFollowRequest(ObjectId senderId, ObjectId receiverId) {
        FollowResponse followResponse = new FollowResponse();
        Optional<Follow> follow = followRepository.findByUser1AndUser2AndStatus(senderId, receiverId,FollowAction.REQUEST);
        if (follow.isEmpty()) {
            followResponse.setMessage("No pending follow request!");
            return followResponse;
        }
        followRepository.delete(follow.get());
        followResponse.setMessage("Follow request rejected!");
        return followResponse;
    }

    @Override
    public FollowResponse unfollow(ObjectId senderId, ObjectId receiverId) {
        FollowResponse followResponse = new FollowResponse();
        Optional<Follow> follow = followRepository.findByUser1AndUser2AndStatus(senderId, receiverId,FollowAction.ACCEPT);
        if (follow.isEmpty()) {
            followResponse.setMessage("You are not following this user!");
            return followResponse;
        }
        followRepository.delete(follow.get());
        followResponse.setMessage("Unfollowed");
        return followResponse;
    }

    @Override
    public FollowResponse cancelFollowRequest(ObjectId senderId, ObjectId receiverId) {
        FollowResponse followResponse = new FollowResponse();
        Optional<Follow> follow = followRepository.findByUser1AndUser2AndStatus(senderId, receiverId,FollowAction.REQUEST);
        if (follow.isEmpty()) {
            followResponse.setMessage("No pending follow request to cancel!");
            return followResponse;
        }
        followRepository.delete(follow.get());
        followResponse.setMessage("Follow request canceled!");
        return followResponse;
    }

    @Override
    public List<BubbleResponse> getFollowing(ObjectId userId) {
        List<BubbleResponse> bubbleResponseList = new ArrayList<>();
        List<Follow> followList = followRepository.getFollowing(userId);
        for (Follow follow : followList) {
            userRepository.findUserByUserID(follow.getUser2()).ifPresent(user -> {
                BubbleResponse bubbleResponse = new BubbleResponse(user.getUserID(), user.getName(), user.getAvatar());
                bubbleResponseList.add(bubbleResponse);
            });
        }
        return bubbleResponseList;
    }

    @Override
    public List<BubbleResponse> getFollowers(ObjectId userId) {
        List<BubbleResponse> bubbleResponseList = new ArrayList<>();
        List<Follow> followList = followRepository.getFollower(userId);
        for (Follow follow : followList) {
            userRepository.findUserByUserID(follow.getUser1()).ifPresent(user -> {
                BubbleResponse bubbleResponse = new BubbleResponse(user.getUserID(), user.getName(), user.getAvatar());
                bubbleResponseList.add(bubbleResponse);
            });
        }
        return bubbleResponseList;
    }

    @Override
    public List<BubbleResponse> getFollowRequests(ObjectId userId) {
        List<BubbleResponse> bubbleResponseList = new ArrayList<>();
        List<Follow> followList = followRepository.getFollowRequests(userId);
        for (Follow follow : followList) {
            userRepository.findUserByUserID(follow.getUser1()).ifPresent(user -> {
                BubbleResponse bubbleResponse = new BubbleResponse(user.getUserID(), user.getName(), user.getAvatar());
                bubbleResponseList.add(bubbleResponse);
            });
        }
        return bubbleResponseList;
    }

    @Override
    public int getFollowersCount(ObjectId userId) {
        return followRepository.countFollowers(userId);
    }

    @Override
    public int getFollowingCount(ObjectId userId) {
        return followRepository.countFollowing(userId);
    }

    @Override
    public boolean isFollowing(ObjectId senderId, ObjectId receiverId) {
        return followRepository.existsByUser1AndUser2AndStatus(senderId, receiverId, FollowAction.ACCEPT);
    }

    @Override
    public String removeFollower(ObjectId senderId, ObjectId receiverId) {
        Optional<Follow> follow = followRepository.findByUser1AndUser2AndStatus(receiverId, senderId,FollowAction.ACCEPT);
        if (follow.isEmpty()) {
            return "Follow record not found!";
        }
        followRepository.delete(follow.get());
        return "Removed";
    }

    @Override
    public boolean hasPendingRequest(ObjectId senderId, ObjectId receiverId) {
        return followRepository.existsByUser1AndUser2AndStatus(senderId, receiverId, FollowAction.REQUEST);
    }
}
