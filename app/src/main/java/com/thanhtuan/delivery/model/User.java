package com.thanhtuan.delivery.model;

import java.util.Date;

/**
 * Created by Nusib on 5/17/2017.
 */

public class User {
    private int userID;
    private String userName;
    private String token;

    public User() {
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
