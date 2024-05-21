package com.noakev.frontend;

/**
 * A class for storing the user that is currently signed in.
 */
public class GlobalUser {
    private static String token;
    private static String username;
    public static String getToken() {
        return token;
    }

    public static String getUsername() {
        return username;
    }

    public static void setToken(String token) {
        GlobalUser.token = token;
    }

    public static void setUsername(String username) {
        GlobalUser.username = username;
    }
}
