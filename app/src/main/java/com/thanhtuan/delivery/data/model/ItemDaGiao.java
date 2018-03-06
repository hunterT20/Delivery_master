package com.thanhtuan.delivery.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ItemDaGiao {
    @SerializedName("SaleReceiptId")
    @Expose
    private String SaleReceiptId;
    @SerializedName("Address")
    @Expose
    private String Address;
    @SerializedName("District")
    @Expose
    private String District;
    @SerializedName("Status")
    @Expose
    private String Status;

    public String getSaleReceiptId() {
        return SaleReceiptId;
    }

    public void setSaleReceiptId(String saleReceiptId) {
        SaleReceiptId = saleReceiptId;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getDistrict() {
        return District;
    }

    public void setDistrict(String district) {
        District = district;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }
}
