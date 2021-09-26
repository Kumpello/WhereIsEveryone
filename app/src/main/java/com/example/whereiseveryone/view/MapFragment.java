package com.example.whereiseveryone.view;

import static com.example.whereiseveryone.utils.GraphicalUtils.getBitmapFromVectorDrawable;

import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.whereiseveryone.R;
import com.example.whereiseveryone.databinding.FragmentMapBinding;
import com.example.whereiseveryone.model.User;
import com.example.whereiseveryone.mvp.BaseFragment;
import com.example.whereiseveryone.presenter.MapPresenter;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MapFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MapFragment extends BaseFragment<MapPresenter> implements OnMapReadyCallback, MapView {

    private GoogleMap mMap;
    private Resources resources;
    private Marker userMarker;
    private Map<String, Marker> friendsMarkers;
    private FragmentMapBinding binding;


    public MapFragment() {
        // Required empty public constructor
    }

    public static MapFragment newInstance() {
        return new MapFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        resources = getResources();

        friendsMarkers = new HashMap<>();

        presenter.startLocationUpdates();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentMapBinding.inflate(inflater, container, false);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map_fragment);
        mapFragment.getMapAsync(this);

        binding.getLocation.setOnClickListener(v -> centerCamera());

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        binding = null;
    }

    @Override
    public void addUserMarker() {
        getActivity().runOnUiThread(() -> userMarker = mMap.addMarker(new MarkerOptions().position(presenter.getUserLatLng())
                .flat(true)
                .anchor(0.5f, 0.5f)
                .rotation(presenter.getAzimuth()).visible(true)
                .icon(BitmapDescriptorFactory.fromBitmap(getBitmapFromVectorDrawable(getActivity().getApplicationContext(),
                        R.drawable.ic_round_navigation_24)))));
    }

    @Override
    public void addFriendsMarker(User user) {
        Log.d("adding friends marker", user.toString());
        Bitmap smallMarker = Bitmap.createScaledBitmap(getBitmapFromVectorDrawable(getActivity().getApplicationContext(),
                R.drawable.ic_friends_map_icon), 30, 30, false);
        getActivity().runOnUiThread(() -> friendsMarkers.put(user.userID, mMap.addMarker(new MarkerOptions().position(user.userLocation)
                .flat(true)
                .anchor(0.5f, 0.5f)
                .title(user.nick)
                .rotation(user.userAzimuth).visible(true)
                .icon(BitmapDescriptorFactory.fromBitmap(smallMarker)))));
    }

    @Override
    public void updateFriendsLocation(User user) {
        getActivity().runOnUiThread(() -> {
            Log.d("updating friends marker", user.toString());
            Marker friendMarker = friendsMarkers.get(user.userID);
            friendMarker.setPosition(user.userLocation);
            friendMarker.setRotation(user.userAzimuth);
        });
    }


    @Override
    public void centerCamera() {
        if (presenter.updateUserLocationAndDirection()) {
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(presenter.getUserLatLng())
                    .zoom(15)
                    .bearing(presenter.getAzimuth())
                    .build();
            getActivity().runOnUiThread(() -> mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition)));
        }
    }

    @Override
    public void updateUserLocation() {
        presenter.updateUserLocationAndDirection();
        getActivity().runOnUiThread(() -> {
            userMarker.setPosition(presenter.getUserLatLng());
            userMarker.setRotation(presenter.getAzimuth());
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        // Don't receive any more updates from either sensor.
        presenter.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        presenter.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        presenter.stopLocationUpdates();
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        mMap.getUiSettings().setAllGesturesEnabled(true);

        presenter.updateLastLocation();
    }

    public void showPermissionDialog(String permission) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getActivity());
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