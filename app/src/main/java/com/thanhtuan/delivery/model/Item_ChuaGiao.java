package com.thanhtuan.delivery.model;

/**
 * Created by Nusib on 5/17/2017.
 */

public class Item_ChuaGiao {
    private String SaleReceiptId;
    private String CustomerName;
    private String PhoneNumber;
    private String Address;
    private String Province;
    private String District;
    private int Quantity;
    private Double Price;
    private String Note;
    private int Status;

    public Item_ChuaGiao() {
    }

    public Item_ChuaGiao(String saleReceiptId, String customerName, String phoneNumber, String address, String province, String district,
                         int quantity, Double price, String note, int status) {
        SaleReceiptId = saleReceiptId;
        CustomerName = customerName;
        PhoneNumber = phoneNumber;
        Address = address;
        Province = province;
        District = district;
        Quantity = quantity;
        Price = price;
        Note = note;
        Status = status;
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
