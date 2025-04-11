package com.example.society.Repository;

import com.example.society.Entity.Account;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface IAccountRepository extends MongoRepository<Account, ObjectId> {
    Optional<Account> findByUsername(String username);
    boolean existsByUsername(String username);
    Optional<Account> findByUserID(ObjectId objectId);
    @Query("{ 'isPrivate': false, 'userID': { $nin: ?0 } }")
    List<Account> findPublicAccountsNotIn(List<ObjectId> excludedUserIDs);
    List<Account> findByIsPrivateFalse();
}
