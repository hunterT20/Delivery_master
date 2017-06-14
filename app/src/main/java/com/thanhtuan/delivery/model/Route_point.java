package com.thanhtuan.delivery.model;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nusib on 5/29/2017.
 */

public class Route_point {
    private String total_distance;
    private String total_duration;
    private LatLng latLng;
    private List<LatLng> overview_polyline;
    private ArrayList<Steps> stepsArrayList;

    public Route_point() {
    }

    public List<LatLng> getOverview_polyline() {
        return overview_polyline;
    }

    public void setOverview_polyline(List<LatLng> overview_polyline) {
        this.overview_polyline = overview_polyline;
    }

    public String getTotal_distance() {
        return total_distance;
    }

    public void setTotal_distance(String total_distance) {
        this.total_distance = total_distance;
    }

    public String getTotal_duration() {
        return total_duration;
    }

    public void setTotal_duration(String total_duration) {
        this.total_duration = total_duration;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public ArrayList<Steps> getStepsArrayList() {
        return stepsArrayList;
    }

    public void setStepsArrayList(ArrayList<Steps> stepsArrayList) {
        this.stepsArrayList = stepsArrayList;
    }
}
