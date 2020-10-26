package com.markstart.rainforest.model;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class Track implements Serializable {

    private final UUID track_id;
    private List<Point> track_points;
//  DataBase Attributes for future iteration to collect more data
//  private String track_name;
//  private String track_description;


    public Track() {
        this.track_id = UUID.randomUUID();
        track_points = new ArrayList<>();
    }


    public UUID getTrack_id() {
        return track_id;
    }

    public List<Point> getTrackPoints() {
        return track_points;
    }


}

