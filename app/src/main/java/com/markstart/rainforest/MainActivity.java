package com.markstart.rainforest;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.markstart.rainforest.client.TrackClient;
import com.markstart.rainforest.dataStorage.DataStorageEngine;


// " any interface is better than no interface " Sensors doesn't need an interface ! All ready provided by android ?
// " any Architecture decisions are better than none " -UoL Study Guide 'Software Engineering'.


public class MainActivity extends AppCompatActivity {

    private Context context = this;
    protected static LocationCallback mLocationCallback;

    private Tracker tracker;
    private DataStorageEngine dse;
    private TrackClient trackClient;

    private boolean mTracking = false;

    protected static TextView mSensorHumidityTextView;
    protected static TextView mSensorTemperatureTextView;
    public static TextView mMessageTextView;
    private TextView mLocationTextView;
//  private TextView mSensorListTextView;
//  private ImageView mAndroidImageViewTracking;

    private Button mSendDataButton;
    private Button mStartButton;
    private Button mStopButton;
    private Button mDeleteButton;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dse = new DataStorageEngine(context);

        mSensorHumidityTextView = (TextView) findViewById(R.id.humidity_sensor_text);
        mSensorTemperatureTextView = (TextView) findViewById(R.id.temperature_sensor_text);
        mLocationTextView = (TextView) findViewById(R.id.location_text);
        mMessageTextView = (TextView) findViewById(R.id.message_text);
//      mSensorListTextView = (TextView) findViewById(R.id.sensor_list);
//      mAndroidImageViewTracking = (ImageView) findViewById(R.id.tracking_image);

        mStartButton = (Button) findViewById(R.id.startButton);
        mStopButton = (Button) findViewById(R.id.stopButton);
        mSendDataButton = (Button) findViewById(R.id.sendButton);
        mDeleteButton = (Button) findViewById(R.id.deleteButton);


        mStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tracker = new Tracker(context);
                mTracking = tracker.startTrackingLocation();
                toggleUI();
            }

        });


        mStopButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                mTracking = tracker.stopTracking(dse);
                toggleUI();
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
                        dse.deleteAllTracks();
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

                trackClient = new TrackClient(context, dse);
                trackClient.sendAllFiles();
            }
        });


        //needs to be at the bottom of onCreate to be able to make periodic location requests
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {

                tracker.setmLastLocation(locationResult.getLastLocation());
                tracker.addNewDataPointToTrack( new Sensors(context) );
                updateScreenDataValues();
            }
        };

    }


    private void toggleUI() {
        if (mTracking) {
            mStartButton.setText("Tracking ...");
            mMessageTextView.setText("");
            mStartButton.setEnabled(false);
            mStopButton.setEnabled(true);
            Toast.makeText(this, " Tracking Started, Saving data every 15 mins ", Toast.LENGTH_LONG).show();

        } else {
            mStartButton.setText("Start Tracking");
            mStartButton.setEnabled(true);
            mStopButton.setEnabled(false);

        }
    }


    private void updateScreenDataValues () {

        mLocationTextView.setText(
                getString(R.string.location_text,
                        tracker.getmLastLocation().getLatitude(),
                        tracker.getmLastLocation().getLongitude(),
                        tracker.getmLastLocation().getTime()));

    }


    public static void messageHandler(String string) {

            mMessageTextView.setText( string );
    }



    protected void showToast(String string) {
        Toast.makeText(context, string, Toast.LENGTH_LONG).show();
    }


    protected static void sensorError()  {
        mSensorHumidityTextView.setText("sensor_error");
    }


    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
    }


}





// ------------------------------ code for future development iterations ---------------------------


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


*/

