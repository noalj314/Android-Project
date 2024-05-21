package com.noakev.frontend.signed_in.event;

/**
 * The event object.
 */
public class Event {
    private String event_id;
    private String username;
    private String photo;
    private String location;
    private String description;

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getPhoto() {
        return photo;
    }

    public String getUsername() {
        return username;
    }

    public String getEvent_id() {
        return event_id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
