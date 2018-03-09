package com.thanhtuan.delivery.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DataPostPhoto {
    @SerializedName("SaleReceiptId")
    @Expose
    private String saleReceiptId;
    @SerializedName("ImageBase64String")
    @Expose
    private String imageBase64String;
    @SerializedName("LocalFileName")
    @Expose
    private String localFileName;
    @SerializedName("FileName")
    @Expose
    private String fileName;
    @SerializedName("ContentType")
    @Expose
    private Object contentType;
    @SerializedName("Extention")
    @Expose
    private Object extention;
    @SerializedName("Width")
    @Expose
    private Integer width;
    @SerializedName("Height")
    @Expose
    private Integer height;

    public String getSaleReceiptId() {
        return saleReceiptId;
    }

    public void setSaleReceiptId(String saleReceiptId) {
        this.saleReceiptId = saleReceiptId;
    }

    public String getImageBase64String() {
        return imageBase64String;
    }

    public void setImageBase64String(String imageBase64String) {
        this.imageBase64String = imageBase64String;
    }

    public String getLocalFileName() {
        return localFileName;
    }

    public void setLocalFileName(String localFileName) {
        this.localFileName = localFileName;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Object getContentType() {
        return contentType;
    }

    public void setContentType(Object contentType) {
        this.contentType = contentType;
    }

    public Object getExtention() {
        return extention;
    }

    public void setExtention(Object extention) {
        this.extention = extention;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }
}
