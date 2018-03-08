package com.thanhtuan.delivery.data.model.map;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LocationMap {
    @SerializedName("lat")
    @Expose
    private Double lat;
    @SerializedName("lng")
    @Expose
    private Double lng;

    public LocationMap(Double lat, Double lng) {
        this.lat = lat;
        this.lng = lng;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    public String getOrigin(){
        return String.valueOf(lat) + "," + String.valueOf(lng);
    }
}
