package com.example.society.Repository;

import com.example.society.Entity.PostEmotion;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface IEmotionRepository extends MongoRepository<PostEmotion, ObjectId> {
}
