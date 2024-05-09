package com.noakev.frontend.signed_in.post;

public class Post {
    private String postimage;
    private String location;
    private String description;

    public Post(String postimage, String location, String description) {
        this.postimage = postimage;
        this.location = location;
        this.description = description;
    }

    public Post() {
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getPostimage() {
        return postimage;
    }

    public void setPostimage(String postimage) {
        this.postimage = postimage;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
