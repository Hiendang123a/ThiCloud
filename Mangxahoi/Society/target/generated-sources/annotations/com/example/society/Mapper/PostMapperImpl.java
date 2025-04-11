package com.example.society.Mapper;

import com.example.society.DTO.Response.PostResponse;
import com.example.society.Entity.Post;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-04-11T23:37:27+0700",
    comments = "version: 1.5.2.Final, compiler: javac, environment: Java 17.0.9 (Oracle Corporation)"
)
@Component
public class PostMapperImpl implements PostMapper {

    @Autowired
    private ConverterMapper converterMapper;

    @Override
    public PostResponse toPostResponse(Post post) {
        if ( post == null ) {
            return null;
        }

        PostResponse postResponse = new PostResponse();

        postResponse.setPostID( converterMapper.objectIDToString( post.getPostID() ) );
        postResponse.setUserID( converterMapper.objectIDToString( post.getUserID() ) );
        postResponse.setEmotions( converterMapper.listObjectIDToListString( post.getEmotions() ) );
        postResponse.setComments( converterMapper.listObjectIDToListString( post.getComments() ) );
        postResponse.setContent( post.getContent() );
        postResponse.setImageUrl( post.getImageUrl() );
        postResponse.setCreatedAt( post.getCreatedAt() );
        postResponse.setEmotionsCount( post.getEmotionsCount() );
        postResponse.setCommentsCount( post.getCommentsCount() );

        return postResponse;
    }

    @Override
    public List<PostResponse> toPostResponseList(List<Post> posts) {
        if ( posts == null ) {
            return null;
        }

        List<PostResponse> list = new ArrayList<PostResponse>( posts.size() );
        for ( Post post : posts ) {
            list.add( toPostResponse( post ) );
        }

        return list;
    }
}
