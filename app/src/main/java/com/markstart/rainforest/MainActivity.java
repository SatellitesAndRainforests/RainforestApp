package com.markstart.rainforest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;



public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager mSensorManager;
    private Sensor mSensorHumidity;
    private Sensor mSensorTemperature;
    private TextView mTextSensorHumidity;
    private TextView mTextSensorTemperature;

    private Button mStartButton;
    private Button mStopButton;
    private TextView mLocationTextView;
    private Location mLastLocation;

    private static final int REQUEST_LOCATION_PERMISSION = 1;

    private FusedLocationProviderClient mFusedLocationClient;

    private ImageView mAndroidImageViewTracking;

    private boolean mTracking = false;

    private LocationCallback mLocationCallback;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mTextSensorHumidity = (TextView) findViewById(R.id.humidity_sensor_text);
        mTextSensorTemperature = (TextView) findViewById(R.id.temperature_sensor_text);
        // for gps --------------------------------------------------
        mLocationTextView = (TextView) findViewById(R.id.location_text);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        // all needed for changing tracking image ?
        mAndroidImageViewTracking = (ImageView) findViewById(R.id.tracking_image);
        // returns null if no Object instance.
        mSensorHumidity = mSensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY);
        mSensorTemperature = mSensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
        mStartButton = (Button) findViewById(R.id.startButton);
        mStopButton = (Button) findViewById(R.id.stopButton);




        mStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mTracking = true;
                tracking();
            }

        });

        mStopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mTracking = false;
                stopTracking();
            }
        });


        //needs to be at the bottom of onCreate NEEDed TO MAKE PERIODIC LOCATION DETAIL REQUESTS
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                mLastLocation = locationResult.getLastLocation();
                mLocationTextView.setText(
                        getString(R.string.location_text,
                                mLastLocation.getLatitude(),
                                mLastLocation.getLongitude(),
                                mLastLocation.getTime()));
            }
        };
    }


    private void tracking() {
        startTrackingLocation();
        startSensors();
        toggleUI();
    }

    private void stopTracking() {
        stopTrackingLocation();
        stopSensors();
        toggleUI();
    }


    private void toggleUI() {
        if (mTracking) {
            mStartButton.setText("Tracking ...");
            mStartButton.setEnabled(false);
            mStopButton.setEnabled(true);
        } else {
            mStartButton.setText("Start Tracking");
            mStartButton.setEnabled(true);
            mStopButton.setEnabled(false);
        }
    }

    private void startSensors() {
        if (mSensorHumidity != null) {
            mSensorManager.registerListener(this, mSensorHumidity, SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            mTextSensorHumidity.setText("sensor_error");
        }

        if (mSensorTemperature != null) {
            mSensorManager.registerListener(this, mSensorTemperature, SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            mTextSensorTemperature.setText("sensor_error");
        }

    }

    private void stopSensors() {
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        int sensorType = sensorEvent.sensor.getType();
        float currentValue = sensorEvent.values[0];

        switch (sensorType) {
            case Sensor.TYPE_RELATIVE_HUMIDITY:
                mTextSensorHumidity.setText(getResources().getString(
                        R.string.humidity_sensor_text, currentValue));
                break;
            case Sensor.TYPE_AMBIENT_TEMPERATURE:
                mTextSensorTemperature.setText(getResources().getString(
                        R.string.temperature_sensor_text, currentValue));
                break;
            default:
        }
    }


    private LocationRequest getLocationRequest() {
        // 10000 = 10 seconds. set gps get requirments.
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(5000); //900000);
        locationRequest.setFastestInterval(2500);//600000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return locationRequest;
    }

    private void startTrackingLocation() {
        // the comditional is automatically suggested by android studio but some of the code, changed to google tutorials code.

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]
                            {Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
        } else {
            mFusedLocationClient.requestLocationUpdates(getLocationRequest(), mLocationCallback, null);
        }
    }

    private void stopTrackingLocation() {
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
    }






    /*

      //  Get List of Sensors.
        List<Sensor> sensorList = mSensorManager.getSensorList(Sensor.TYPE_ALL);
        StringBuilder sensorText = new StringBuilder();
        for (Sensor currentSensor : sensorList) {
            sensorText.append(currentSensor.getName()).append(
                    System.getProperty("line.separator"));
        }
        TextView sensorTextView = (TextView) findViewById(R.id.sensor_list);
        sensorTextView.setText(sensorText);


            // GPS BUTTON request location permission at runtime , NEEDED ??______________________________________

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_LOCATION_PERMISSION:
                // If the permission is granted, get the location,
                // otherwise, show a Toast
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getLocation();
                    startButton.setText("getting location");
                } else {
                    Toast.makeText(this,
                            R.string.location_permission_denied,
                            Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void flashTrackingImage() {
            mAndroidImageViewTracking.setVisibility(View.VISIBLE);
            mAndroidImageViewTracking.setVisibility(View.INVISIBLE);
    }


*/




    // leave empty if not implementing. Needed because implementing interface.
    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    /*
    //register sensor listeneres, override onCreate to listen to sensors even when app is in background uses battery
    @Override
    protected void onStart() {
        super.onStart();
    }

    //unregister all listerners when app is paused, stop battery consumption
    @Override
    protected void onStop() {
        super.onStop();
//        mSensorManager.unregisterListener(this);
    }
*/

}


















