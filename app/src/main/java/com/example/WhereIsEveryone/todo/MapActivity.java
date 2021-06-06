package com.example.WhereIsEveryone.todo;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.WhereIsEveryone.R;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.Timer;
import java.util.TimerTask;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback, SensorEventListener {

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private Location userLocation;
    private LocationRequest userLocationRequest;
    private Resources resources;
    private Timer timer;
    private Marker userMarker;
    private SensorManager sensorManager;
    private boolean requestingLocationUpdates;
    private final float[] accelerometerReading = new float[3];
    private final float[] magnetometerReading = new float[3];
    private final float[] rotationMatrix = new float[9];
    private final float[] orientationAngles = new float[3];
    private LocationCallback locationCallback;
    private float azimuth;
    private ImageButton getLocationButton;

    private final int FASTEST_INTERVAL = 500;
    private final int INTERVAL = 2000;
    private final float m = (float) (180 / Math.PI);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        userLocationRequest = LocationRequest.create();
        userLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        userLocationRequest.setInterval(INTERVAL);
        userLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        resources = getResources();
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        timer = new Timer();
        requestingLocationUpdates = true;
        getLocationButton = findViewById(R.id.getLocation);


        getLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                centerCamera();
            }
        });

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull @org.jetbrains.annotations.NotNull LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                userLocation = locationResult.getLastLocation();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateOrientationAngles();
                        azimuth = getAzimuth(orientationAngles[0]);
                        userMarker.setPosition(new LatLng(userLocation.getLatitude(), userLocation.getLongitude()));
                        userMarker.setRotation(azimuth);
                        Toast.makeText(getApplicationContext(), String.valueOf(orientationAngles[0] * 100), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        };

        // TODO(kumpel): This will change in next commit I hope,
        //               so I left this activity without checking etc.
        //               (see LoginActivity to see how we should ask for permission).
//        askForPermissions(Manifest.permission.ACCESS_COARSE_LOCATION, resources.getString(R.string.location_permission_text));
//        askForPermissions(Manifest.permission.ACCESS_FINE_LOCATION, resources.getString(R.string.location_permission_text));

        startLocationUpdates();


        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                getLastLocation();
                updateOrientationAngles();
                azimuth = getAzimuth(orientationAngles[0]);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        userMarker = mMap.addMarker(new MarkerOptions().position(
                                new LatLng(userLocation.getLatitude(), userLocation.getLongitude()))
                                .flat(true)
                                .anchor(0.5f,0.5f)
                                .rotation(azimuth).visible(true)
                                .icon(BitmapDescriptorFactory.fromBitmap(getBitmapFromVectorDrawable(getApplicationContext(), R.drawable.ic_round_navigation_24))));
                    }
                });
                centerCamera();
            }
        }, 500);

    }

    public float getAzimuth(float orientationAngle) {
        if (orientationAngles[0] >= 0) {
            return orientationAngles[0] * m;
        } else {
            return 360 + (orientationAngles[0] * m);
        }
    }

    public Bitmap getBitmapFromVectorDrawable(Context context, int drawableId) {
        Drawable drawable = ContextCompat.getDrawable(context, drawableId);
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }


    private void centerCamera() {
        if (userLocation != null) {
            getLastLocation();
            updateOrientationAngles();
            LatLng userLocationLatLng = new LatLng(userLocation.getLatitude(), userLocation.getLongitude());
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(userLocationLatLng)
                            .zoom(15)
                            .bearing(orientationAngles[0])
                            .build();
                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

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

    @SuppressLint("MissingPermission")
    private void startLocationUpdates() {
        fusedLocationClient.requestLocationUpdates(userLocationRequest,
                locationCallback,
                Looper.getMainLooper());
    }

    private void stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Don't receive any more updates from either sensor.
        sensorManager.unregisterListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopLocationUpdates();
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

    public void updateOrientationAngles() {
        // Update rotation matrix, which is needed to update orientation angles.
        SensorManager.getRotationMatrix(rotationMatrix, null,
                accelerometerReading, magnetometerReading);
        // "rotationMatrix" now has up-to-date information.
        SensorManager.getOrientation(rotationMatrix, orientationAngles);
        // "orientationAngles" now has up-to-date information.
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1 || requestCode == 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
                centerCamera();
            }
        }
    }

    @SuppressLint("MissingPermission")
    private void getLastLocation(){
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            userLocation = location;
                        }
                    }
                });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        mMap.getUiSettings().setAllGesturesEnabled(true);

        getLastLocation();
        if (userLocation != null) {
            LatLng userLocationLatLng = new LatLng(userLocation.getLatitude(), userLocation.getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLng(userLocationLatLng));
        }
    }
}