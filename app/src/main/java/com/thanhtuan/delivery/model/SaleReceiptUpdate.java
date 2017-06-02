package com.thanhtuan.delivery.model;

import java.util.List;

/**
 * Created by Nusib on 5/23/2017.
 */

public class SaleReceiptUpdate {
    private String SaleReceiptId;
    private List<URL_PhotoUpload> Photos;

    public SaleReceiptUpdate(String saleReceiptId, List<URL_PhotoUpload> url) {
        SaleReceiptId = saleReceiptId;
        this.Photos = url;
    }

    public SaleReceiptUpdate() {
    }

    public String getSaleReceiptId() {
        return SaleReceiptId;
    }

    public void setSaleReceiptId(String saleReceiptId) {
        SaleReceiptId = saleReceiptId;
    }

    public List<URL_PhotoUpload> getUrl() {
        return Photos;
    }

    public void setUrl(List<URL_PhotoUpload> url) {
        this.Photos = url;
    }
}
