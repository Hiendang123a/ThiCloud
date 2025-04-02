package com.example.society.Repository;

import com.example.society.Entity.Post;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface IPostRepository extends MongoRepository<Post, ObjectId> {
}
