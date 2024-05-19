package com.noakev.frontend.backend;

import com.noakev.frontend.signed_in.comment.Comment;
import com.noakev.frontend.signed_in.event.Event;
import com.noakev.frontend.signed_in.profile.User;

import java.util.ArrayList;

public class APIObject {
    private String status;
    private String username;
    private String password;
    private String description;
    private String token;
    private String message;
    private ArrayList<Event> events;
    private ArrayList<Comment> comments;
    private ArrayList<String> followers;
    private ArrayList<String> following;
    private ArrayList<String> users_liked;
    ArrayList<String> grupper;

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getDescription() {
        return description;
    }

    public String getToken() {
        return token;
    }
    public String getMessage() {
        return message;
    }

    public ArrayList<Event> getEvents() {
        return events;
    }
    public ArrayList<Comment> getComments() {
        return comments;
    }

    public ArrayList<String> getFollowersList() {
        return followers;
    }

    public ArrayList<String> getFollowingList() {
        return following;
    }

    public ArrayList<String> getUsers_liked() {
        return users_liked;
    }

    public ArrayList<String> getGrupper() {
        return grupper;
    }
    public boolean statusFail() {
        return status.equals("fail");
    }
}
