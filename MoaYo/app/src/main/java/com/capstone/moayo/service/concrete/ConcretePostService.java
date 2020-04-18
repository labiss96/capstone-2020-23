package com.capstone.moayo.service.concrete;

import android.content.Context;

import com.capstone.moayo.entity.Post;
import com.capstone.moayo.service.PostService;
import com.capstone.moayo.service.dto.PostDto;
import com.capstone.moayo.storage.PostStorage;
import com.capstone.moayo.storage.concrete.StorageFactoryCreator;

import java.util.List;

public class ConcretePostService implements PostService {
    private PostStorage contentStorage;

    public ConcretePostService(Context context) {
        contentStorage = StorageFactoryCreator.getInstance().requestContentStorage(context);
    }

    @Override
    public String createPost(PostDto newPostDto) {
        Post newPost = newPostDto.toPost();
        return null;
    }

    @Override
    public List<PostDto> findPostByCategoryNodeId(int id) {
        return null;
    }

    @Override
    public PostDto findPostById(int id) {
        return null;
    }

    @Override
    public boolean modifyPost(PostDto postDto) {
        return false;
    }

    @Override
    public void deletePostById(int id) {

    }
}
