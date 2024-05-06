package com.noakev.frontend.logged_in.profile;


import java.util.ArrayList;

public class Groups {
    ArrayList<String> grupper;
    public ArrayList<String> getUsers(){
        return grupper;
    }

    public void setMembers(ArrayList<String> grupper) {
        this.grupper = grupper;
    }
}
