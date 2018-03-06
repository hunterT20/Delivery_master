package com.thanhtuan.delivery.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ItemChuaGiao {
    @SerializedName("SaleReceiptId")
    @Expose
    private String SaleReceiptId;
    @SerializedName("CustomerName")
    @Expose
    private String CustomerName;
    @SerializedName("PhoneNumber")
    @Expose
    private String PhoneNumber;
    @SerializedName("AddressFull")
    @Expose
    private String Address;
    @SerializedName("Province")
    @Expose
    private String Province;
    @SerializedName("District")
    @Expose
    private String District;
    @SerializedName("Quantity")
    @Expose
    private int Quantity;
    @SerializedName("Price")
    @Expose
    private Double Price;
    @SerializedName("Note")
    @Expose
    private String Note;
    @SerializedName("Status")
    @Expose
    private int Status;

    public ItemChuaGiao() {
    }

    public String getSaleReceiptId() {
        return SaleReceiptId;
    }

    public void setSaleReceiptId(String saleReceiptId) {
        SaleReceiptId = saleReceiptId;
    }

    public String getCustomerName() {
        return CustomerName;
    }

    public void setCustomerName(String customerName) {
        CustomerName = customerName;
    }

    public String getPhoneNumber() {
        return PhoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        PhoneNumber = phoneNumber;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getProvince() {
        return Province;
    }

    public void setProvince(String province) {
        Province = province;
    }

    public String getDistrict() {
        return District;
    }

    public void setDistrict(String district) {
        District = district;
    }

    public int getQuantity() {
        return Quantity;
    }

    public void setQuantity(int quantity) {
        Quantity = quantity;
    }

    public Double getPrice() {
        return Price;
    }

    public void setPrice(Double price) {
        Price = price;
    }

    public String getNote() {
        return Note;
    }

    public void setNote(String note) {
        Note = note;
    }

    public int getStatus() {
        return Status;
    }

    public void setStatus(int status) {
        Status = status;
    }
}
