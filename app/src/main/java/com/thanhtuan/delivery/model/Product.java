package com.thanhtuan.delivery.model;

/**
 * Created by Nusib on 5/23/2017.
 */

public class Product {
    private String ItemId;
    private String SKU;
    private int Quantity;
    private Double Price;
    private int Status;

    public Product(String itemId, String SKU, int quantity, Double price, int status) {
        ItemId = itemId;
        this.SKU = SKU;
        Quantity = quantity;
        Price = price;
        Status = status;
    }

    public Product() {
    }

    public String getItemId() {
        return ItemId;
    }

    public void setItemId(String itemId) {
        ItemId = itemId;
    }

    public String getSKU() {
        return SKU;
    }

    public void setSKU(String SKU) {
        this.SKU = SKU;
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

    public int getStatus() {
        return Status;
    }

    public void setStatus(int status) {
        Status = status;
    }
}
