package com.example.society.DTO.Response;

import com.mongodb.lang.Nullable;
import lombok.Getter;
import org.bson.types.ObjectId;

@Getter
public class BubbleResponse {
    private String userID;
    private String name;
    private String avatar;
    public BubbleResponse() {
    }
    public BubbleResponse(ObjectId userID, String name, @Nullable String avatar) {
        this.userID = userID.toHexString();
        this.name = name;
        this.avatar = (avatar != null) ? avatar : ""; // Nếu null thì đặt thành chuỗi rỗng
    }
    public BubbleResponse(ObjectId userID, String name) {
        this.userID = userID.toHexString();
        this.name = name;
        this.avatar = ""; // Đặt giá trị mặc định nếu không có avatar
    }
    public void setAvatar(@Nullable String avatar) {
        this.avatar = avatar;
    }
}
