package com.thanhtuan.delivery.model;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

/**
 * Created by Nusib on 5/29/2017.
 */

public class Steps {
    private String distance;
    private String duration;
    private String html_instructions;
    private LatLng start_location;
    private LatLng end_location;
    private List<LatLng> polyline;

    public Steps(String distance, String duration, String html_instructions, LatLng start_location, LatLng end_location, List<LatLng> polyline) {
        this.distance = distance;
        this.duration = duration;
        this.html_instructions = html_instructions;
        this.start_location = start_location;
        this.end_location = end_location;
        this.polyline = polyline;
    }

    public Steps() {
    }

    public List<LatLng> getPolyline() {
        return polyline;
    }

    public void setPolyline(List<LatLng> polyline) {
        this.polyline = polyline;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getHtml_instructions() {
        return html_instructions;
    }

    public void setHtml_instructions(String html_instructions) {
        this.html_instructions = html_instructions;
    }

    public LatLng getStart_location() {
        return start_location;
    }

    public void setStart_location(LatLng start_location) {
        this.start_location = start_location;
    }

    public LatLng getEnd_location() {
        return end_location;
    }

    public void setEnd_location(LatLng end_location) {
        this.end_location = end_location;
    }
}
