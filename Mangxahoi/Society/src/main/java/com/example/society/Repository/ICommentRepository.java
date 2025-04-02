package com.example.society.Repository;

import com.example.society.Entity.PostComment;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ICommentRepository extends MongoRepository<PostComment, ObjectId> {
}
