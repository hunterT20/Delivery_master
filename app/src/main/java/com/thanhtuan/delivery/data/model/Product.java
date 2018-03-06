package com.thanhtuan.delivery.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Product {
    @SerializedName("ItemId")
    @Expose
    private String ItemId;
    @SerializedName("ItemName")
    @Expose
    private String ItemName;
    @SerializedName("Quantity")
    private int Quantity;

    public String getItemId() {
        return ItemId;
    }

    public void setItemId(String itemId) {
        ItemId = itemId;
    }

    public String getItemName() {
        return ItemName;
    }

    public void setItemName(String itemName) {
        this.ItemName = itemName;
    }

    public int getQuantity() {
        return Quantity;
    }

    public void setQuantity(int quantity) {
        Quantity = quantity;
    }
}
