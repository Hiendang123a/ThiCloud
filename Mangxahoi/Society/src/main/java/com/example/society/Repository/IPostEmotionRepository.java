package com.example.society.Repository;

import com.example.society.Entity.PostEmotion;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface IPostEmotionRepository extends MongoRepository<PostEmotion, ObjectId> {
    Optional<PostEmotion> findByPostIDAndUserID(ObjectId postID, ObjectId userID);
}
