package com.noakev.frontend;

import java.util.ArrayList;

public class Posts {
    private ArrayList<User> medlemmar = new ArrayList<>();
    public ArrayList getUsers(){
        return medlemmar;
    }

    public StringBuilder getUsersAsString() {
        return new StringBuilder();
    }

    public void setMembers(ArrayList<User> medlemmar) {
        this.medlemmar = medlemmar;
    }

}
