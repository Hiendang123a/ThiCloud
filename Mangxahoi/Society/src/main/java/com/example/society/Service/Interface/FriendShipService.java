package com.example.society.Service.Interface;

import com.example.society.DTO.Response.BubbleResponse;
import org.bson.types.ObjectId;

import java.util.List;

public interface FriendShipService {
    public List<BubbleResponse> getFriendsList(ObjectId userId);
}
