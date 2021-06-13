package com.example.WhereIsEveryone.view;

import androidx.annotation.NonNull;

import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;

import com.example.WhereIsEveryone.R;
import com.example.WhereIsEveryone.databinding.ActivityMapBinding;
import com.example.WhereIsEveryone.mvp.BaseActivity;
import com.example.WhereIsEveryone.presenter.MapPresenter;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.Timer;
import java.util.TimerTask;

import static com.example.WhereIsEveryone.utils.GraphicalUtils.getBitmapFromVectorDrawable;

public class MapActivity extends BaseActivity<MapPresenter> implements OnMapReadyCallback, MapView {

    private GoogleMap mMap;
    private Timer timer;
    private boolean requestingLocationUpdates;
    private Resources resources;
    private Marker userMarker;


    private ActivityMapBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMapBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        //setContentView(R.layout.activity_map); PROBLEM SOURCE!

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_fragment);
        mapFragment.getMapAsync(this);


        requestingLocationUpdates = true;
        timer = new Timer();
        resources = getResources();

        binding.getLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                centerCamera();
            }
        });

        presenter.startLocationUpdates();

        // Todo
        // Move timer to presenter, make method bo be called in Activity

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                presenter.getLastLocation();
                presenter.updateUserLocationAndDirection();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        userMarker = mMap.addMarker(new MarkerOptions().position(presenter.getUserLatLng())
                                .flat(true)
                                .anchor(0.5f,0.5f)
                                .rotation(presenter.getAzimuth()).visible(true)
                                .icon(BitmapDescriptorFactory.fromBitmap(getBitmapFromVectorDrawable(getApplicationContext(),
                                        R.drawable.ic_round_navigation_24))));
                    }
                });
            }
        }, 500);

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                updateUserLocation();
            }
        }, 1000, 500);

    }

    public void centerCamera() {
        if (presenter.updateUserLocationAndDirection()) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(presenter.getUserLatLng())
                            .zoom(15)
                            .bearing(presenter.getAzimuth())
                            .build();
                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                }
            });
        }
    }

    @Override
    public void updateUserLocation() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                presenter.updateUserLocationAndDirection();
                userMarker.setPosition(presenter.getUserLatLng());
                userMarker.setRotation(presenter.getAzimuth());
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Don't receive any more updates from either sensor.
        presenter.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        presenter.stopLocationUpdates();
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        mMap.getUiSettings().setAllGesturesEnabled(true);

        presenter.getLastLocation();
        if (presenter.updateUserLocationAndDirection()) {
            LatLng userLocationLatLng = presenter.getUserLatLng();
            mMap.moveCamera(CameraUpdateFactory.newLatLng(userLocationLatLng));
        }
    }

    public void showPermissionDialog(String permission) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        builder.setTitle(resources.getString(R.string.permission_needed));
        builder.setMessage(resources.getString(R.string.location_permission_text));
        builder.setNegativeButton(resources.getString(R.string.decline),
                (dialog, which) -> dialog.dismiss());
        builder.setPositiveButton(resources.getString(R.string.accept), (dialog, which) -> {
            //Where to store request code?
            requestPermissions(new String[]{permission}, 2137);
            dialog.dismiss();
        });
        builder.show();
    }

    @Override
    public void requestPermission(String permission) {
        requestPermissions(new String[]{permission}, 2137);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            presenter.getLastLocation();
            centerCamera();
        }
    }
}