package com.example.society.Repository;
import com.example.society.Entity.Follow;
import com.example.society.Entity.FollowAction;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface IFollowRepository extends MongoRepository<Follow, ObjectId> {
    Optional<Follow> findByUser1AndUser2AndStatus(ObjectId user1, ObjectId user2, FollowAction status);

    Optional<Follow> findByUser1AndUser2(ObjectId user1, ObjectId user2);

    @Query(value = "{ 'user1': ?0, 'status': 'ACCEPT' }")
    List<Follow> getFollowing(ObjectId userId);

    @Query(value = "{ 'user2': ?0, 'status': 'ACCEPT' }")
    List<Follow> getFollower(ObjectId userId);

    @Query(value = "{ 'user2': ?0, 'status': 'REQUEST' }")
    List<Follow> getFollowRequests(ObjectId userId);

    @Query(value = "{ 'user1': ?0, 'status': 'ACCEPT' }", count = true)
    int countFollowing(ObjectId userId);

    @Query(value = "{ 'user2': ?0, 'status': 'ACCEPT' }", count = true)
    int countFollowers(ObjectId userId);

    boolean existsByUser1AndUser2AndStatus(ObjectId user1, ObjectId user2, FollowAction status);
}
