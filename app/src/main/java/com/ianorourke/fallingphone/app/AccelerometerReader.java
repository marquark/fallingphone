package com.ianorourke.fallingphone.app;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import android.util.Log;

public class AccelerometerReader implements SensorEventListener {
    private float lastX, lastY, lastZ;
    private boolean hasSensor = false;
    private boolean initialized = false;
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private final float NOISE = 2.0f;

    public interface AccelerometerReaderInterface {
        public void receivedValues(float x, float y, float z);
    }

    private AccelerometerReaderInterface accelerometerReaderInterface;

    public AccelerometerReader(Context c) {
        sensorManager = (SensorManager) c.getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
    }

    @Override
    public final void onAccuracyChanged(Sensor sensor, int accuracy) {
        Log.v("accel", "Accuracy Changed: " + accuracy);
    }

    @Override
    public final void onSensorChanged(SensorEvent event) {
        lastX = event.values[0];
        lastY = event.values[1];
        lastZ = event.values[2];

        if (accelerometerReaderInterface != null) accelerometerReaderInterface.receivedValues(lastX, lastY, lastZ);
    }

    public void setEnabled(boolean b) {
        if (b) {
            if (accelerometer != null && !initialized) {
                hasSensor = true;

                sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
                initialized = true;
            } else {
                hasSensor = false;
            }
        } else {
            if (initialized) {
                sensorManager.unregisterListener(this);
                initialized = false;
            }
        }
    }

    public void setInterface(AccelerometerReaderInterface i) {
        accelerometerReaderInterface = i;
    }

    public float getX() {
        if (hasSensor) return lastX;
        else return 0.0f;
    }

    public float getY() {
        if (hasSensor) return lastY;
        else return 0.0f;
    }

    public float getZ() {
        if (hasSensor) return lastZ;
        else return 0.0f;
    }
}
