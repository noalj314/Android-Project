package com.noakev.frontend;

public class GlobalUser {
    private static String token;
    private static String username;

    public GlobalUser(String token, String username) {
        this.token = token;
        this.username = username;
    }

    public static String getToken() {
        return token;
    }

    public static String getUsername() {
        return username;
    }
}
