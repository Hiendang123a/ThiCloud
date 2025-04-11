package com.example.society.Repository;

import com.example.society.Entity.Post;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface IPostRepository extends MongoRepository<Post, ObjectId> {
    Optional<List<Post>> findAllByUserIDOrderByCreatedAtDesc(ObjectId userID);

    Optional<Post> findByPostID(ObjectId postID);

    Page<Post> findByUserIDIn(List<ObjectId> userIDs, Pageable pageable);

    @Query("{ 'userID': { $in: ?0 }}")
    Page<Post> findTrendingFromOtherUsers(List<ObjectId> publicUserIDs,Pageable pageable);

    @Query("{ 'userID': { $in: ?0 }}")
    Page<Post> findTrendingPostsFromPublicAccounts(List<ObjectId> publicUserIDs, Pageable pageable);
}
