package com.thanhtuan.delivery.model;

import java.util.Date;

/**
 * Created by Nusib on 5/17/2017.
 */

public class User {
    private String ID;
    private int userID;
    private String userName;
    private String LoginDate;
    private String ExpiredDate;

    public User() {
    }

    public User(String ID, int userID, String userName, String loginDate, String expiredDate) {
        this.ID = ID;
        this.userID = userID;
        this.userName = userName;
        LoginDate = loginDate;
        ExpiredDate = expiredDate;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
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

    public String getLoginDate() {
        return LoginDate;
    }

    public void setLoginDate(String loginDate) {
        LoginDate = loginDate;
    }

    public String getExpiredDate() {
        return ExpiredDate;
    }

    public void setExpiredDate(String expiredDate) {
        ExpiredDate = expiredDate;
    }
}
