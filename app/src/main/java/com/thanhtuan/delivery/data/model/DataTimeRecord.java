package com.thanhtuan.delivery.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DataTimeRecord {
    @SerializedName("SaleReceipId")
    @Expose
    private String SaleReceipId;
    @SerializedName("Status")
    @Expose
    private int Status;

    public String getSaleReceipId() {
        return SaleReceipId;
    }

    public void setSaleReceipId(String saleReceipId) {
        SaleReceipId = saleReceipId;
    }

    public int getStatus() {
        return Status;
    }

    public void setStatus(int status) {
        Status = status;
    }
}
