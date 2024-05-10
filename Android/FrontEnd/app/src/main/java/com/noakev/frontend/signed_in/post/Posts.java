package com.noakev.frontend.signed_in.post;

import com.noakev.frontend.signed_in.profile.User;

import java.util.ArrayList;
import java.util.HashMap;

public class Posts {
    private ArrayList<Post> events = new ArrayList<>();
    public ArrayList getUsers(){
        return events;
    }

    public StringBuilder getUsersAsString() {
        return new StringBuilder();
    }

    public void setMembers(ArrayList<Post> medlemmar) {
        this.events = medlemmar;
    }

    public ArrayList<Post> getEvents() {
        return events;
    }

}
