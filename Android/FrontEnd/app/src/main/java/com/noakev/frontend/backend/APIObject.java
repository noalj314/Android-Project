package com.noakev.frontend.backend;

import com.noakev.frontend.signed_in.comment.Comment;
import com.noakev.frontend.signed_in.event.Event;

import java.util.ArrayList;

/**
 * The APIObject that contains all necassary fields we use when
 * receiving data from the database and want to retrieve it with Gson.
 */
public class APIObject {
    private String status;
    private String username;
    private String password;
    private String description;
    private String token;
    private String message;
    private String amount_of_likes;
    private ArrayList<Event> events;
    private ArrayList<Comment> comments;
    private ArrayList<String> followers;
    private ArrayList<String> following;
    private ArrayList<String> users_liked;

    public String getUsername() {
        return username;
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
    public String getAmount_of_likes() {
        return amount_of_likes;
    }
    public boolean statusFail() {
        return status.equals("fail");
    }
}
