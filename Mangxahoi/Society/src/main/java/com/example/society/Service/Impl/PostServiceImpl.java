package com.example.society.Service.Impl;

import com.example.society.DTO.Response.PostResponse;
import com.example.society.Entity.Account;
import com.example.society.Entity.Follow;
import com.example.society.Entity.Post;
import com.example.society.Mapper.PostMapper;
import com.example.society.Repository.IAccountRepository;
import com.example.society.Repository.IFollowRepository;
import com.example.society.Repository.IPostRepository;
import com.example.society.Service.Interface.PostService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PostServiceImpl implements PostService {
    @Autowired
    IPostRepository postRepository;
    @Autowired
    PostMapper postMapper;
    @Autowired
    IFollowRepository followRepository;

    @Autowired
    IAccountRepository accountRepository;

    @Autowired
    MessageServiceImpl messageService;


    @Override
    public List<String> getUserPostImages(ObjectId userID) {
        Optional<List<Post>> postList = postRepository.findAllByUserIDOrderByCreatedAtDesc(userID);
        if (postList.isPresent()) {
            List<PostResponse> postResponseList = postMapper.toPostResponseList(postList.get());
            return postResponseList.stream()
                    .map(PostResponse::getImageUrl)
                    .collect(Collectors.toList());
        }
        return null;
    }

    @Override
    public List<PostResponse> getHomeFeedPostsForUser(ObjectId userID, int page, int size) {
        List<Follow> followList = followRepository.getFollowing(userID);
        List<ObjectId> followObjectIds = followList.stream().map(Follow::getUser2).toList();
        System.out.println("followObjectIds: " + followObjectIds);
        List<ObjectId> validFollowIds = new ArrayList<>();
        for (ObjectId followObjectId : followObjectIds) {
            Optional<Account> account = accountRepository.findByUserID(followObjectId);
            if(account.isPresent()){
                if(account.get().isPrivate())
                {
                    System.out.println("followObjectId: " + followObjectId);
                    if(messageService.isMutualFollow(userID, followObjectId))
                        validFollowIds.add(followObjectId);
                }
                else
                    validFollowIds.add(followObjectId);
            }
        }
        Pageable pageable = PageRequest.of(page, size, Sort.by(
                                Sort.Order.desc("emotionsCount"),
                                Sort.Order.desc("commentsCount"),
                                Sort.Order.desc("createdAt")));
        Page<Post> postsPage = postRepository.findByUserIDIn(validFollowIds, pageable);
        List<Post> posts = new ArrayList<>(postsPage.getContent());

        int remaining = size - posts.size();

        if (remaining > 0) {
            List<ObjectId> publicUserIDs = accountRepository.findPublicAccountsNotIn(followObjectIds).stream()
                    .map(Account::getUserID)
                    .toList();
            int trendingOffset;
            if(postsPage.getTotalElements() > 0)
                trendingOffset = ((page+ 1) * size - (int)(postsPage.getTotalElements()))/size;
            else
                trendingOffset = (page * size - (int)(postsPage.getTotalElements()))/size;
            Page<Post> trendingPageData = postRepository.findTrendingFromOtherUsers(
                    publicUserIDs,
                    PageRequest.of(trendingOffset, size, Sort.by(
                            Sort.Order.desc("emotionsCount"),
                            Sort.Order.desc("commentsCount"),
                            Sort.Order.desc("createdAt")))
            );
            posts.addAll(trendingPageData.getContent());
        }
        return postMapper.toPostResponseList(posts);
    }

    @Override
    public List<PostResponse> getHomeFeedPostsForGuest(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(
                Sort.Order.desc("emotionsCount"),
                Sort.Order.desc("commentsCount"),
                Sort.Order.desc("createdAt")));
        List<ObjectId> publicUserIDs = accountRepository.findByIsPrivateFalse().stream()
                .map(Account::getUserID)
                .toList();
        Page<Post> postsPage = postRepository.findTrendingPostsFromPublicAccounts(publicUserIDs,pageable);
        List<Post> posts = new ArrayList<>(postsPage.getContent());
        return postMapper.toPostResponseList(posts);
    }
}
