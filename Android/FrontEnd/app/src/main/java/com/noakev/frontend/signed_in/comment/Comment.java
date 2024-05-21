package com.noakev.frontend.signed_in.comment;

/**
 * Comment object.
 */
public class Comment {
    private String id;
    private String username;
    private String event_id;
    private String text;

    public String getId() {
        return id;
    }

    public String getEvent_id() {
        return event_id;
    }

    public String getUsername() {
        return username;
    }

    public String getText() {
        return text;
    }
}
