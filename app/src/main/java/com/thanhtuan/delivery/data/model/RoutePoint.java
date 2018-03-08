package com.thanhtuan.delivery.data.model;

import com.google.android.gms.maps.model.LatLng;
import com.thanhtuan.delivery.data.model.map.StepMap;

import java.util.List;

public class RoutePoint {
    private String total_distance;
    private String total_duration;
    private LatLng latLng;
    private List<LatLng> overview_polyline;
    private List<Steps> stepsArrayList;

    public RoutePoint() {
    }

    public List<LatLng> getOverviewPolyline() {
        return overview_polyline;
    }

    public void setOverviewPolyline(List<LatLng> overview_polyline) {
        this.overview_polyline = overview_polyline;
    }

    public String getTotalDistance() {
        return total_distance;
    }

    public void setTotalDistance(String total_distance) {
        this.total_distance = total_distance;
    }

    public String getTotalDuration() {
        return total_duration;
    }

    public void setTotalDuration(String total_duration) {
        this.total_duration = total_duration;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public List<Steps> getStepsArrayList() {
        return stepsArrayList;
    }

    public void setStepsArrayList(List<Steps> stepsArrayList) {
        this.stepsArrayList = stepsArrayList;
    }
}
