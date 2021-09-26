package com.example.whereiseveryone.model;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Looper;

import androidx.annotation.NonNull;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import static android.content.Context.SENSOR_SERVICE;

public class MapServiceImpl implements MapService, SensorEventListener {

    private final Activity activity;

    private final FusedLocationProviderClient fusedLocationClient;
    private Location userLocation;
    private final LocationRequest userLocationRequest;
    private final SensorManager sensorManager;
    private final boolean requestingLocationUpdates;
    private boolean locationCallbackReady;
    private final LocationCallback locationCallback;

    private final float[] accelerometerReading = new float[3];
    private final float[] magnetometerReading = new float[3];
    private final float[] rotationMatrix = new float[9];
    private final float[] orientationAngles = new float[3];

    public MapServiceImpl(Activity activity) {
        this.activity = activity;
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity);
        userLocationRequest = LocationRequest.create();
        int FASTEST_INTERVAL = 500;
        userLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        int INTERVAL = 2000;
        userLocationRequest.setInterval(INTERVAL);
        userLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        sensorManager = (SensorManager) activity.getSystemService(SENSOR_SERVICE);
        requestingLocationUpdates = true;
        locationCallbackReady = false;

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull @org.jetbrains.annotations.NotNull LocationResult locationResult) {
                userLocation = locationResult.getLastLocation();
                locationCallbackReady = true;
            }
        };
    }

    @Override
    public boolean locationCallbackReady(){
        return locationCallbackReady;
    }

    @SuppressLint("MissingPermission")
    public void updateLastLocation(){
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(activity, location -> {
                    if (location != null) {
                        userLocation = location;
                    }
                });
    }

    @Override
    public boolean updateUserLocationAndDirection() {
        if (userLocation != null) {
            updateLastLocation();
            updateOrientationAngles();

            return true;
        } else {
            return false;
        }
    }

    @Override
    public Location getLocation() {
        return userLocation;
    }

    public void updateOrientationAngles() {
        // Update rotation matrix, which is needed to update orientation angles.
        SensorManager.getRotationMatrix(rotationMatrix, null,
                accelerometerReading, magnetometerReading);
        // "rotationMatrix" now has up-to-date information.
        SensorManager.getOrientation(rotationMatrix, orientationAngles);
        // "orientationAngles" now has up-to-date information.
    }

    @Override
    public float getAzimuth() {
        float m = (float) (180 / Math.PI);
        if (orientationAngles[0] >= 0) {
            return orientationAngles[0] * m;
        } else {
            return 360 + (orientationAngles[0] * m);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            System.arraycopy(event.values, 0, accelerometerReading,
                    0, accelerometerReading.length);
        } else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            System.arraycopy(event.values, 0, magnetometerReading,
                    0, magnetometerReading.length);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @SuppressLint("MissingPermission")
    public void startLocationUpdates() {
        fusedLocationClient.requestLocationUpdates(userLocationRequest,
                locationCallback,
                Looper.getMainLooper());
    }

    public void stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }

    @Override
    public void onPause() {
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onResume() {
        Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if (accelerometer != null) {
            sensorManager.registerListener(this, accelerometer,
                    SensorManager.SENSOR_DELAY_NORMAL, SensorManager.SENSOR_DELAY_UI);
        }
        Sensor magneticField = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        if (magneticField != null) {
            sensorManager.registerListener(this, magneticField,
                    SensorManager.SENSOR_DELAY_NORMAL, SensorManager.SENSOR_DELAY_UI);
        }
        if (requestingLocationUpdates) {
            startLocationUpdates();
        }
    }
}
