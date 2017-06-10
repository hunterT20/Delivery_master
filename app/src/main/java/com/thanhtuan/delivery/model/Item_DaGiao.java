package com.thanhtuan.delivery.model;

/**
 * Created by Nusib on 6/10/2017.
 */

public class Item_DaGiao {
    private String SaleReceiptId;
    private String Address;
    private String District;
    private String Status;

    public Item_DaGiao(String saleReceiptId, String address, String district, String status) {
        SaleReceiptId = saleReceiptId;
        Address = address;
        District = district;
        Status = status;
    }

    public Item_DaGiao() {
    }

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
