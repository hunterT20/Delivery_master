package com.thanhtuan.delivery.data.model.map;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class StepMap {
    @SerializedName("distance")
    @Expose
    private Distance distance;
    @SerializedName("duration")
    @Expose
    private Duration duration;
    @SerializedName("end_location")
    @Expose
    private LocationMap endLocation;
    @SerializedName("html_instructions")
    @Expose
    private String htmlInstructions;
    @SerializedName("polyline")
    @Expose
    private Point polyline;
    @SerializedName("start_location")
    @Expose
    private LocationMap startLocation;
    @SerializedName("travel_mode")
    @Expose
    private String travelMode;
    @SerializedName("maneuver")
    @Expose
    private String maneuver;

    public Distance getDistance() {
        return distance;
    }

    public void setDistance(Distance distance) {
        this.distance = distance;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public LocationMap getEndLocation() {
        return endLocation;
    }

    public void setEndLocation(LocationMap endLocation) {
        this.endLocation = endLocation;
    }

    public String getHtmlInstructions() {
        return htmlInstructions;
    }

    public void setHtmlInstructions(String htmlInstructions) {
        this.htmlInstructions = htmlInstructions;
    }

    public Point getPolyline() {
        return polyline;
    }

    public void setPolyline(Point polyline) {
        this.polyline = polyline;
    }

    public LocationMap getStartLocation() {
        return startLocation;
    }

    public void setStartLocation(LocationMap startLocation) {
        this.startLocation = startLocation;
    }

    public String getTravelMode() {
        return travelMode;
    }

    public void setTravelMode(String travelMode) {
        this.travelMode = travelMode;
    }

    public String getManeuver() {
        return maneuver;
    }

    public void setManeuver(String maneuver) {
        this.maneuver = maneuver;
    }
}
