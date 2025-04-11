package com.example.society.Repository;

import com.example.society.Entity.PostComment;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ICommentRepository extends MongoRepository<PostComment, ObjectId> {
    Optional<PostComment> findByCommentID(ObjectId objectId);
}
