package com.thanhtuan.delivery.data.model.map;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Bounds {
    @SerializedName("northeast")
    @Expose
    private LocationMap northeast;
    @SerializedName("southwest")
    @Expose
    private LocationMap southwest;

    public LocationMap getNortheast() {
        return northeast;
    }

    public void setNortheast(LocationMap northeast) {
        this.northeast = northeast;
    }

    public LocationMap getSouthwest() {
        return southwest;
    }

    public void setSouthwest(LocationMap southwest) {
        this.southwest = southwest;
    }
}
