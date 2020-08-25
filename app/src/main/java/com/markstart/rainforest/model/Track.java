package com.markstart.rainforest.model;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Track implements Serializable {

    private UUID track_id;
    private String track_name;
    private String track_description;
    private List<Point> points;


    public Track() {
        this.track_id = UUID.randomUUID();
        points = new ArrayList<Point>();
    }

    public UUID getTrack_id() {
        return track_id;
    }

    public void setTrack_id(UUID track_id) {
        this.track_id = track_id;
    }

    public String getTrack_name() {
        return track_name;
    }

    public void setTrack_name(String track_name) {
        this.track_name = track_name;
    }

    public String getTrack_description() {
        return track_description;
    }

    public void setTrack_description(String track_description) {
        this.track_description = track_description;
    }

    public List<Point> getPoints() {
        return points;
    }

    public void setPoints(List<Point> points) {
        this.points = points;
    }
}

