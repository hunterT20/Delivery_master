package com.thanhtuan.delivery.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DataSentSMS {
    @SerializedName("Phone")
    @Expose
    private String Phone;
    @SerializedName("SoPhut")
    @Expose
    private String SoPhut;

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public String getSoPhut() {
        return SoPhut;
    }

    public void setSoPhut(String soPhut) {
        SoPhut = soPhut;
    }
}
