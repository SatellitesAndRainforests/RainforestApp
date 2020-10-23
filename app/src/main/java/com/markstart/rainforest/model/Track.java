package com.markstart.rainforest.model;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationManager;

import androidx.core.app.ActivityCompat;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Track implements Serializable {

    private UUID track_id;
//    private String track_name;
//    private String track_description;
    private List<Point> track_points;


    public Track() {
        this.track_id = UUID.randomUUID();
        track_points = new ArrayList<Point>();
    }




    public UUID getTrack_id() {
        return track_id;
    }

    public void setTrack_id(UUID track_id) {
        this.track_id = track_id;
    }

    public List<Point> getTrack_points() {
        return track_points;
    }

    public void setTrack_points(List<Point> track_points) {
        this.track_points = track_points;
    }
}

