package com.example.whereiseveryone.view;

import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;

import com.example.whereiseveryone.R;
import com.example.whereiseveryone.databinding.ActivityMapBinding;
import com.example.whereiseveryone.mvp.BaseActivity;
import com.example.whereiseveryone.presenter.MapPresenter;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import static com.example.whereiseveryone.utils.GraphicalUtils.getBitmapFromVectorDrawable;

public class MapActivity extends BaseActivity<MapPresenter> implements OnMapReadyCallback, MapView {

    private GoogleMap mMap;
    private Resources resources;
    private Marker userMarker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        com.example.whereiseveryone.databinding.ActivityMapBinding binding = ActivityMapBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_fragment);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);


        resources = getResources();

        binding.getLocation.setOnClickListener(v -> centerCamera());

        presenter.startLocationUpdates();

    }

    public void addUserMarker(){
        runOnUiThread(() -> userMarker = mMap.addMarker(new MarkerOptions().position(presenter.getUserLatLng())
                .flat(true)
                .anchor(0.5f,0.5f)
                .rotation(presenter.getAzimuth()).visible(true)
                .icon(BitmapDescriptorFactory.fromBitmap(getBitmapFromVectorDrawable(getApplicationContext(),
                        R.drawable.ic_round_navigation_24)))));
    }

    public void centerCamera() {
        if (presenter.updateUserLocationAndDirection()) {
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(presenter.getUserLatLng())
                    .zoom(15)
                    .bearing(presenter.getAzimuth())
                    .build();
            runOnUiThread(() -> mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition)));
        }
    }

    @Override
    public void updateUserLocation() {
        presenter.updateUserLocationAndDirection();
        runOnUiThread(() -> {
            userMarker.setPosition(presenter.getUserLatLng());
            userMarker.setRotation(presenter.getAzimuth());
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

        presenter.updateLastLocation();
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
            presenter.updateLastLocation();
            centerCamera();
        }
    }
}