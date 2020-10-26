package com.markstart.rainforest;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.markstart.rainforest.dataStorage.DataStorageEngine;
import com.markstart.rainforest.dataStorage.TrackFileNameCreator;
import com.markstart.rainforest.model.Point;
import com.markstart.rainforest.model.Track;

import java.sql.Timestamp;
import java.util.UUID;


public class Tracker {

    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private FusedLocationProviderClient mFusedLocationClient;
    private Location mLastLocation;
    private Track track;
    private Context trackerContext;


    public Tracker(Context context) {

        track = new Track();
        trackerContext = context;
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(trackerContext);

    }


    protected boolean startTrackingLocation(){

            // the conditional is automatically suggested by android studio but some of the code follows the google android studio tutorial for permissions in android sensors.
        boolean startedTracking = false;

        try {

            if (ActivityCompat.checkSelfPermission(trackerContext,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions((Activity) trackerContext, new String[]
                                {Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_LOCATION_PERMISSION);
            } else {
                mFusedLocationClient.requestLocationUpdates(getLocationRequest(), MainActivity.mLocationCallback, null);
                startedTracking = true;
            }
        } catch (Exception e) {
            startedTracking = false;
        }
        finally {
            return startedTracking;
        }

    }


    private LocationRequest getLocationRequest() {

        // 5000 = 5 seconds. finalize software project requirements for sensor performance / timing  before programming.
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(900000);    // 900000 is 15 minutes, good for prototype/ testing : )
        locationRequest.setFastestInterval(900000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return locationRequest;

    }



    @RequiresApi(api = Build.VERSION_CODES.O)
    protected boolean stopTracking(DataStorageEngine dse) {

        boolean stoppedTracking = stopTrackingLocation();
        String trackFileName = TrackFileNameCreator.createFileName(track);
        boolean saved = dse.saveTrackToFile(track, trackFileName);

        if (saved) {
            Toast.makeText(trackerContext, " Tracking File Saved ", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(trackerContext, " Could not save file ", Toast.LENGTH_SHORT).show();
        }

        return stoppedTracking;

    }


    private boolean stopTrackingLocation() {

            try {
                mFusedLocationClient.removeLocationUpdates(MainActivity.mLocationCallback);
            } catch (Exception e ) {
                return true;
            }
            return false;

    }


    protected void addNewDataPointToTrack( Sensors sensors ) {

        getTrack().getTrackPoints().add( new Point(
                        UUID.randomUUID(),
                        getTrack().getTrack_id(),
                        (float) getmLastLocation().getLatitude(),
                        (float) getmLastLocation().getLongitude(),
                        new Timestamp( getmLastLocation().getTime()),
                        sensors.getCurrentHumidity(),
                        sensors.getCurrentTemperature())
                );

    }



    public Track getTrack() {
        return track;
    }

    public Location getmLastLocation() {
        return mLastLocation;
    }

    public void setmLastLocation(Location mLastLocation) {
        this.mLastLocation = mLastLocation;
    }


}
