package com.noakev.frontend.backend;

import com.noakev.frontend.signed_in.event.Event;

import java.util.ArrayList;

public class APIObject {
    private String username;
    private String password;
    private String description;
    private String token;
    private ArrayList<Event> events;
    private ArrayList<String> followers;
    private ArrayList<String> following;
    private ArrayList<String> users_liked;

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
}
