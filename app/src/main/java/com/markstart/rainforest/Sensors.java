package com.markstart.rainforest;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.widget.Toast;


public class Sensors implements SensorEventListener {

    private boolean haveSensorReadings;
    private boolean haveHumidityReading;
    private boolean haveTemperatureReading;
    private SensorManager mSensorManager;
    private Sensor mSensorHumidity;
    private Sensor mSensorTemperature;
    private float currentHumidity;
    private float currentTemperature;
    private Context sensorContext;
    private Tracker sensorTracker;


    public Sensors(Context context, Tracker tracker) {

        sensorContext = context;
        sensorTracker = tracker;

        mSensorManager = (SensorManager) context.getSystemService(context.SENSOR_SERVICE);

        // Implement a UI list for all sensors
        mSensorHumidity = mSensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY);
        mSensorTemperature = mSensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);

        getSensorDataThread thread = new getSensorDataThread();
        thread.start();
    }


    private class getSensorDataThread extends Thread {

        private getSensorDataThread() {
            haveHumidityReading = false;
            haveTemperatureReading = false;
            haveSensorReadings = false;
        }

        @Override
        public void run() {

            startSensors();

            while (haveSensorReadings == false) {
                if (haveTemperatureReading && haveHumidityReading) {
                    haveSensorReadings = true;
                }
            }

            sensorTracker.addNewDataPointToTrack(currentHumidity, currentTemperature);
            stopSensors();
            return;
        }
    }


    private void startSensors() {

        if (mSensorHumidity != null) {
            mSensorManager.registerListener( this, mSensorHumidity, SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            MainActivity.sensorError();
        }

        if (mSensorTemperature != null) {
            mSensorManager.registerListener( this , mSensorTemperature, SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            MainActivity.sensorError();
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

                currentHumidity = currentValue;

                MainActivity.mSensorHumidityTextView
                        .setText(sensorContext.getResources()
                                .getString(R.string.humidity_sensor_text, currentValue));

                haveHumidityReading = true;



                break;

            case Sensor.TYPE_AMBIENT_TEMPERATURE:

                currentTemperature = currentValue;

                MainActivity.mSensorTemperatureTextView
                        .setText(sensorContext.getResources()
                                .getString(R.string.temperature_sensor_text, currentValue));

                haveTemperatureReading = true;

                break;

            default:
        }

    }



    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }


    public float getCurrentHumidity() {
        return currentHumidity;
    }

    public float getCurrentTemperature() {
        return currentTemperature;
    }


}
