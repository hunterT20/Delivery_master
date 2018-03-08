package com.thanhtuan.delivery.data.model.map;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Leg {
    @SerializedName("distance")
    @Expose
    private Distance distance;
    @SerializedName("duration")
    @Expose
    private Duration duration;
    @SerializedName("end_address")
    @Expose
    private String endAddress;
    @SerializedName("end_location")
    @Expose
    private LocationMap endLocation;
    @SerializedName("start_address")
    @Expose
    private String startAddress;
    @SerializedName("start_location")
    @Expose
    private LocationMap startLocation;
    @SerializedName("steps")
    @Expose
    private List<StepMap> steps = null;
    @SerializedName("traffic_speed_entry")
    @Expose
    private List<Object> trafficSpeedEntry;
    @SerializedName("via_waypoint")
    @Expose
    private List<Object> viaWaypoint;

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

    public String getEndAddress() {
        return endAddress;
    }

    public void setEndAddress(String endAddress) {
        this.endAddress = endAddress;
    }

    public LocationMap getEndLocation() {
        return endLocation;
    }

    public void setEndLocation(LocationMap endLocation) {
        this.endLocation = endLocation;
    }

    public String getStartAddress() {
        return startAddress;
    }

    public void setStartAddress(String startAddress) {
        this.startAddress = startAddress;
    }

    public LocationMap getStartLocation() {
        return startLocation;
    }

    public void setStartLocation(LocationMap startLocation) {
        this.startLocation = startLocation;
    }

    public List<StepMap> getSteps() {
        return steps;
    }

    public void setSteps(List<StepMap> steps) {
        this.steps = steps;
    }

    public List<Object> getTrafficSpeedEntry() {
        return trafficSpeedEntry;
    }

    public void setTrafficSpeedEntry(List<Object> trafficSpeedEntry) {
        this.trafficSpeedEntry = trafficSpeedEntry;
    }

    public List<Object> getViaWaypoint() {
        return viaWaypoint;
    }

    public void setViaWaypoint(List<Object> viaWaypoint) {
        this.viaWaypoint = viaWaypoint;
    }
}
