package com.noakev.frontend.signed_in.event;

import com.noakev.frontend.signed_in.profile.User;

public class Event {
    /*
            'id': self.id,
            'title': self.title,
            'created_by_user_id': self.created_by_user_id,
            'location': self.location,
            'date': self.date,
            'description': self.description
     */
    private User username;
    private String photo;
    private String location;
    private String description;

    public Event(String photo, String location, String description) {
        this.photo = photo;
        this.location = location;
        this.description = description;
    }

    public Event() {
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getPhoto() {
        return photo;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
