package com.example.society.Repository;

import com.example.society.DTO.Response.BubbleResponse;
import com.example.society.Entity.User;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface IUserRepository extends MongoRepository<User, ObjectId> {
    Optional<User> findUserByUserID(ObjectId id);
    @Query(value = "{ '_id': { $in: ?0 } }", fields = "{ '_id': 1, 'name': 1, 'avatar': 1 }")
    List<BubbleResponse> findUsersByIds(List<ObjectId> userIds);
    Page<User> findByNameContainingIgnoreCase(String name, Pageable pageable);
}
