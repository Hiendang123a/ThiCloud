package com.example.society.Service.Impl;

import com.example.society.DTO.Response.BubbleResponse;
import com.example.society.Entity.FriendShip;
import com.example.society.Repository.IFriendShipRepository;
import com.example.society.Repository.IUserRepository;
import com.example.society.Service.Interface.FriendShipService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FriendShipServiceImpl implements FriendShipService {
    @Autowired
    IFriendShipRepository friendShipRepository;

    @Autowired
    IUserRepository userRepository;

    @Override
    public List<BubbleResponse> getFriendsList(ObjectId userId) {
        List<FriendShip> friendships = friendShipRepository.findFriendsByUserId(userId);
        List<ObjectId> friendIds = friendships.stream()
                .map(f -> f.getUser1().equals(userId) ? f.getUser2() : f.getUser1())
                .collect(Collectors.toList());
        friendIds.add(0, userId);
        List<BubbleResponse> friendsList = userRepository.findUsersByIds(friendIds);
        friendsList.sort((a, b) -> a.getUserID().equals(userId.toString()) ? -1 : 1);
        return friendsList;
    }
}
