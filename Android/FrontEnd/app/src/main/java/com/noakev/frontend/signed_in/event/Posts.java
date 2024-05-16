package com.noakev.frontend.signed_in.event;

import java.util.ArrayList;

public class Posts {
    private ArrayList<Event> events = new ArrayList<>();
    public ArrayList getUsers(){
        return events;
    }

    public StringBuilder getUsersAsString() {
        return new StringBuilder();
    }

    public void setMembers(ArrayList<Event> medlemmar) {
        this.events = medlemmar;
    }

    public ArrayList<Event> getEvents() {
        return events;
    }

}
