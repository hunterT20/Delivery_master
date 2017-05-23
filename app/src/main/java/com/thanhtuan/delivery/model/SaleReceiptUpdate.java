package com.thanhtuan.delivery.model;

import java.util.List;

/**
 * Created by Nusib on 5/23/2017.
 */

public class SaleReceiptUpdate {
    private String SaleReceiptId;
    private String Description;
    private List<Photo> photo;
    private int Status;

    public SaleReceiptUpdate(String saleReceiptId, String description, List<Photo> photo, int status) {
        SaleReceiptId = saleReceiptId;
        Description = description;
        this.photo = photo;
        Status = status;
    }

    public SaleReceiptUpdate() {
    }

    public String getSaleReceiptId() {
        return SaleReceiptId;
    }

    public void setSaleReceiptId(String saleReceiptId) {
        SaleReceiptId = saleReceiptId;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public List<Photo> getPhoto() {
        return photo;
    }

    public void setPhoto(List<Photo> photo) {
        this.photo = photo;
    }

    public int getStatus() {
        return Status;
    }

    public void setStatus(int status) {
        Status = status;
    }
}
