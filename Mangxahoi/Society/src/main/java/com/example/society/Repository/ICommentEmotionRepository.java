package com.example.society.Repository;

import com.example.society.Entity.CommentEmotion;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ICommentEmotionRepository extends MongoRepository<CommentEmotion, ObjectId> {
}
