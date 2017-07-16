package com.sareen.squarelabs.techygeekadmin.model;

/**
 * Created by ashish on 16/7/17.
 */

public class Post
{
    String postId;
    String mainImageUrl;
    String title;
    String text;

    public Post()
    {
    }

    public Post(String postId, String mainImageUrl, String title, String text)
    {
        this.postId = postId;
        this.mainImageUrl = mainImageUrl;
        this.title = title;
        this.text = text;
    }

    public String getPostId()
    {
        return postId;
    }

    public String getMainImageUrl()
    {
        return mainImageUrl;
    }

    public String getTitle()
    {
        return title;
    }

    public String getText()
    {
        return text;
    }
}