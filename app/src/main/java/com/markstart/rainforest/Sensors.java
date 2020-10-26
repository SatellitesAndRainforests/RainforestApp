package com.markstart.rainforest;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;


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


    public Sensors(Context context) {

        sensorContext = context;

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

            while (!haveSensorReadings) {
                if (haveTemperatureReading && haveHumidityReading) {
                    haveSensorReadings = true;
                }
            }

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

                MainActivity.mSensorHumidityTextView
                        .setText(sensorContext.getResources()
                                .getString(R.string.humidity_sensor_text, currentValue));

                haveHumidityReading = true;
                currentHumidity = currentValue;

                break;

            case Sensor.TYPE_AMBIENT_TEMPERATURE:

                MainActivity.mSensorTemperatureTextView
                        .setText(sensorContext.getResources()
                                .getString(R.string.temperature_sensor_text, currentValue));

                haveTemperatureReading = true;
                currentTemperature = currentValue;

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
