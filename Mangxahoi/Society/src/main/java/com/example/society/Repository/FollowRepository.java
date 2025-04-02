package com.example.society.Repository;


import com.example.society.Entity.Follow;
import com.example.society.Entity.FollowAction;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FollowRepository extends MongoRepository<Follow, ObjectId> {
    /*
    // Lấy danh sách những người user đang follow (đã ACCEPT)
    @Query("{'senderUsername': ?0, 'action': 'ACCEPT'}")
    List<String> findFollowing(String username);

    // Lấy danh sách những người follow user (đã ACCEPT)
    @Query("{'receiverUsername': ?0, 'action': 'ACCEPT'}")
    List<String> findFollowers(String username);

    // Lấy danh sách yêu cầu follow đang chờ duyệt (REQUEST)
    @Query("{'receiverUsername': ?0, 'action': 'REQUEST'}")
    List<String> findFollowRequests(String username);

    // Tìm Follow theo sender và receiver
    Follow findBySenderUsernameAndReceiverUsername(String senderUsername, String receiverUsername);

    // Tìm FollowRequest theo sender và receiver (chờ duyệt)
    @Query("{'senderUsername': ?0, 'receiverUsername': ?1, 'action': 'REQUEST'}")
    Follow findFollowRequest(String senderUsername, String receiverUsername);

    // Lấy số lượng followers
    @Query("{$match: {'receiverUsername': ?0, 'action': 'ACCEPT'}}")
    long countFollowers(String username);

    // Lấy số lượng following
    @Query("{$match: {'senderUsername': ?0, 'action': 'ACCEPT'}}")
    long countFollowing(String username);

    // Kiểm tra xem user đã follow chưa
    boolean existsBySenderUsernameAndReceiverUsername(String senderUsername, String receiverUsername);

    // Kiểm tra xem đã có yêu cầu follow với action nào đó chưa
    boolean existsBySenderUsernameAndReceiverUsernameAndAction(String senderUsername, String receiverUsername, FollowAction action);
     */
}

