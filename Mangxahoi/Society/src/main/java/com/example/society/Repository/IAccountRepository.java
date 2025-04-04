package com.example.society.Repository;

import com.example.society.Entity.Account;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import org.springframework.data.domain.Pageable;
import java.util.Optional;

public interface IAccountRepository extends MongoRepository<Account, ObjectId> {
    Optional<Account> findByUsername(String username);
    boolean existsByUsername(String username);
    Optional<Account> findByUserID(ObjectId objectId);


    /*
    @Aggregation(pipeline = {
            "{ $match: { 'username': { $regex: ?0, $options: 'i' } } }",
            "{ $lookup: { from: 'users', localField: 'userID', foreignField: '_id', as: 'user' } }",
            "{ $unwind: '$user' }",
            "{ $project: { 'username': 1, 'user.name': 1, 'user.avatar': 1 } }"
    })
    Page<FollowInfo> findFollowInfoByUsernameRegex(String query, Pageable pageable);
     */
}
