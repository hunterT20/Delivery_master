package com.thanhtuan.delivery.data.model.map;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Point {
    @SerializedName("points")
    @Expose
    private String points;

    public String getPoints() {
        return points;
    }

    public void setPoints(String points) {
        this.points = points;
    }
}
