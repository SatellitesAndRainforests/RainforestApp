package com.markstart.rainforest;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.markstart.rainforest.model.Point;
import com.markstart.rainforest.model.Track;
import com.markstart.rainforest.dataStorage.DataStorageEngine;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.UUID;

import static com.markstart.rainforest.client.TrackClient.sendJsonPoint;
import static com.markstart.rainforest.dataStorage.TrackFileNameCreator.createFileName;


// any interface is better than no interface !!
// any archetecture decisions are  better than none !


public class MainActivity extends AppCompatActivity implements SensorEventListener {


    private static final int REQUEST_LOCATION_PERMISSION = 1;

    private boolean mTracking = false;

    boolean gotSensorReadings;
    boolean gotHumidityReading;
    boolean gotTemperatureReading;

    String tempFilename;

    float currentHumidity;
    float currentTemperature;

    private Track track;

    private Context context = this;

    private SensorManager mSensorManager;
    private FusedLocationProviderClient mFusedLocationClient;

    private LocationCallback mLocationCallback;

    private Sensor mSensorHumidity;
    private Sensor mSensorTemperature;

    private Location mLastLocation;

    private TextView mSensorHumidityTextView;
    private TextView mSensorTemperatureTextView;
    private TextView mLocationTextView;
    private TextView mSensorListTextView;
    private static TextView mMessageTextView;

    private Button mSendDataButton;
    private Button mStartButton;
    private Button mStopButton;
    private Button mDeleteButton;

    private ImageView mAndroidImageViewTracking;

    DataStorageEngine dse;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        mSensorHumidity = mSensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY);
        mSensorTemperature = mSensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);

        mSensorHumidityTextView = (TextView) findViewById(R.id.humidity_sensor_text);
        mSensorTemperatureTextView = (TextView) findViewById(R.id.temperature_sensor_text);
        mLocationTextView = (TextView) findViewById(R.id.location_text);
       // mSensorListTextView = (TextView) findViewById(R.id.sensor_list);
        mMessageTextView = (TextView) findViewById(R.id.message_text);

        mStartButton = (Button) findViewById(R.id.startButton);
        mStopButton = (Button) findViewById(R.id.stopButton);
        mSendDataButton = (Button) findViewById(R.id.sendButton);
        mDeleteButton = (Button) findViewById(R.id.deleteButton);

        mAndroidImageViewTracking = (ImageView) findViewById(R.id.tracking_image);

        dse = new DataStorageEngine();

        mStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mTracking = true;
                tracking();
                mMessageTextView.setText("");
            }

        });

        mStopButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                mTracking = false;
                stopTracking();
            }
        });

        mDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Delete all files : They can't be recovered.");
                builder.setMessage("Are you sure ?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dse.deleteAllTracks(context);
                    }
                });

                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();



            }
        });

        mSendDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(context, " Connecting to server ... ... ... ", Toast.LENGTH_LONG).show();
                mMessageTextView.setText("Connecting ...");


                ArrayList<Track> tracks = dse.getAllTracksFromDisk(context);

                    for (Track track : tracks) {
                        for (Point point : track.getTrack_points()) {
                            sendJsonPoint(context, point);
                        }
                    }

                //    mSendDataButton.setEnabled(false);


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

                Log.d("locationCallback", " before get sensors");
                getSensorDataThread thread = new getSensorDataThread();
                thread.start();

                track.getTrack_points().add(makePoint());

            }
        };
    }




    public static void messageHandler(Boolean sent) {
        if (sent) {
            mMessageTextView.setText("Data sent !");
        } else {
            mMessageTextView.setText("Data could not be sent");
        }
    }


    private Point makePoint() {
        Point point;

        point = new Point(
                UUID.randomUUID(),
                track.getTrack_id(),
                (float) mLastLocation.getLatitude(),
                (float) mLastLocation.getLongitude(),
                new Timestamp(mLastLocation.getTime()),
                currentHumidity,
                currentTemperature);

        return point;
    }


    private void tracking() {
        track = new Track();
        startTrackingLocation();
        toggleUI();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void stopTracking() {
        stopTrackingLocation();
        String fileName = createFileName(track);
        tempFilename = fileName;

        boolean saved = dse.saveTrackToFile( context, track, fileName);
        if (saved) {
            Toast.makeText(this, " Tracking File Saved ", Toast.LENGTH_LONG).show();

        } else {
         Toast.makeText(this, " Could not save file ", Toast.LENGTH_SHORT).show();
        }
        toggleUI();

     //   Track t = getTrackFromFile(context, fileName);
     //   boolean fileISNull = (t == null);
     //   Log.d("fileIsNull" + fileISNull,"");
     //   mSensorListTextView.append(getTrackFromFile(context, fileName).getPoints().get(0).getJSONObject().toString());

    }



    class getSensorDataThread extends Thread {

        public getSensorDataThread() {
            gotHumidityReading = false;
            gotTemperatureReading = false;
            gotSensorReadings = false;
        }

        @Override
        public void run() {

                startSensors();

                while (gotSensorReadings == false) {
                    if (gotTemperatureReading && gotHumidityReading) {
                        gotSensorReadings = true;
                    }
                }

                stopSensors();
                Log.d("UI THREAD","end");
                return;
            }
        }





    private void toggleUI() {
        if (mTracking) {
            mStartButton.setText("Tracking ...");
            mStartButton.setEnabled(false);
            mStopButton.setEnabled(true);
            Toast.makeText(this, " Tracking Started, Saving data every 15 mins ", Toast.LENGTH_LONG).show();

        } else {
            mStartButton.setText("Start Tracking");
            mStartButton.setEnabled(true);
            mStopButton.setEnabled(false);
        //    mSendDataButton.setEnabled(true);

        }
    }

    private void startSensors() {
        if (mSensorHumidity != null) {
            mSensorManager.registerListener(this, mSensorHumidity, SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            mSensorHumidityTextView.setText("sensor_error");

        }

        if (mSensorTemperature != null) {
            mSensorManager.registerListener(this, mSensorTemperature, SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            mSensorTemperatureTextView.setText("sensor_error");
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
                mSensorHumidityTextView.setText(getResources().getString(
                        R.string.humidity_sensor_text, currentValue));
                gotHumidityReading = true;
                currentHumidity = currentValue;

                break;
            case Sensor.TYPE_AMBIENT_TEMPERATURE:
                mSensorTemperatureTextView.setText(getResources().getString(
                        R.string.temperature_sensor_text, currentValue));
                gotTemperatureReading = true;
                currentTemperature = currentValue;
                break;
            default:
        }
    }


    private LocationRequest getLocationRequest() {
        // 5000 = 5 seconds. set gps get requirments.
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(900000); //900000 is 15 mins );
        locationRequest.setFastestInterval(900000);
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


















