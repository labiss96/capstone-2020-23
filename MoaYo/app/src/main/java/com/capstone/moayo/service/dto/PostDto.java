package com.capstone.moayo.service.dto;

import com.capstone.moayo.entity.Post;

public class PostDto {
    private int id;
    private String imgUrl;
    private String url;
    private String hashtag;
    private int like;

    private int categoryNodeId;

    public PostDto() {
        this.id = 0;
    }
    public PostDto(String imgUrl, String url, String hashtag, int like) {
        this.imgUrl = imgUrl;
        this.url = url;
        this.hashtag = hashtag;
        this.like = like;
    }

    public Post toPost() {
        Post post = new Post(imgUrl, url, hashtag, like);
        post.setCategoryNodeId(categoryNodeId);
        post.setId(id);


        return post;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getHashtag() {
        return hashtag;
    }

    public void setHashtag(String hashtag) {
        this.hashtag = hashtag;
    }

    public int getLike() {
        return like;
    }

    public void setLike(int like) {
        this.like = like;
    }

    public int getCategoryNodeId() {
        return categoryNodeId;
    }

    public void setCategoryNodeId(int categoryNodeId) {
        this.categoryNodeId = categoryNodeId;
    }
}
