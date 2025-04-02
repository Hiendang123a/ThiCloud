package com.example.society.Repository;
import com.example.society.Entity.Follow;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface IFollowRepository extends MongoRepository<Follow, ObjectId> {
    @Query(value = "{ $or: [ { 'user1': ?0 }, { 'user2': ?0 } ] }", fields = "{ 'user1': 1, 'user2': 1 }")
    List<Follow> findFriendsByUserId(ObjectId userId);
}
