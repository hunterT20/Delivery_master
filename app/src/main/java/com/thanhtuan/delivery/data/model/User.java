package com.thanhtuan.delivery.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class User {
    @SerializedName("FullName")
    @Expose
    private String fullName;
    @SerializedName("Birthday")
    @Expose
    private String birthday;
    @SerializedName("EmployeeId")
    @Expose
    private String employeeId;
    @SerializedName("PassWord")
    @Expose
    private Object passWord;
    @SerializedName("SessionToken")
    @Expose
    private String sessionToken;
    @SerializedName("UserTypeId")
    @Expose
    private Integer userTypeId;

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public Object getPassWord() {
        return passWord;
    }

    public void setPassWord(Object passWord) {
        this.passWord = passWord;
    }

    public String getSessionToken() {
        return sessionToken;
    }

    public void setSessionToken(String sessionToken) {
        this.sessionToken = sessionToken;
    }

    public Integer getUserTypeId() {
        return userTypeId;
    }

    public void setUserTypeId(Integer userTypeId) {
        this.userTypeId = userTypeId;
    }
}
