package com.kumpello.whereiseveryone.view;

import static com.kumpello.whereiseveryone.utils.GraphicalUtils.getBitmapFromVectorDrawable;
import static com.kumpello.whereiseveryone.utils.TextUtils.isNullOrEmpty;

import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

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
import com.kumpello.whereiseveryone.R;
import com.kumpello.whereiseveryone.databinding.FragmentMapBinding;
import com.kumpello.whereiseveryone.model.User;
import com.kumpello.whereiseveryone.mvp.BaseFragment;
import com.kumpello.whereiseveryone.presenter.MapPresenter;
import com.kumpello.whereiseveryone.utils.OnResult;
import com.kumpello.whereiseveryone.utils.TextUtils;

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
    private CameraPosition cameraPosition;
    private Float currentZoom;
    //Get this field to common settings file
    private static final float INITIAL_ZOOM = 18;
    private boolean centerCamera;


    public MapFragment() {
        // Required empty public constructor
    }

    public static MapFragment newInstance() {
        return new MapFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentZoom = INITIAL_ZOOM;
        centerCamera = false;
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
        binding.zoomIn.setOnClickListener(v -> zoomIn());
        binding.zoomOut.setOnClickListener(v -> zoomOut());
        binding.mapType.setOnClickListener(v -> changeMapType());
        binding.addNotification.setOnClickListener(v -> addNotification());

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
                .zIndex(0.7f)
                .flat(true)
                .anchor(0.5f, 0.5f)
                .rotation(presenter.getAzimuth()).visible(true)
                .icon(BitmapDescriptorFactory.fromBitmap(getBitmapFromVectorDrawable(getActivity().getApplicationContext(),
                        R.drawable.ic_round_navigation_24)))));
    }
    //Todo Change icons for markers?
    @Override
    public void addFriendsMarker(User user) {
        Log.d("adding friends marker", user.toString());
        Bitmap smallMarker = Bitmap.createScaledBitmap(getBitmapFromVectorDrawable(getActivity().getApplicationContext(),
                R.drawable.ic_friends_map_icon), 30, 30, false);
        getActivity().runOnUiThread(() -> friendsMarkers.put(user.userID, mMap.addMarker(new MarkerOptions().position(user.userLocation)
                .zIndex(1f)
                .alpha(0.9f)
                .flat(true)
                .anchor(0.5f, 0.5f)
                .title(user.nick)
                .snippet(user.message)
                .rotation(user.userAzimuth).visible(true)
                .icon(BitmapDescriptorFactory.fromBitmap(smallMarker)))));
    }

    @Override
    public void updateFriendsLocation(User user) {
        getActivity().runOnUiThread(() -> {
            Log.d("updating friends marker", user.toString() + "    " + user.message);
            Marker friendMarker = friendsMarkers.get(user.userID);
            friendMarker.setTitle(user.nick);
            friendMarker.setPosition(user.userLocation);
            friendMarker.setRotation(user.userAzimuth);
            friendMarker.setSnippet(user.message);
        });
    }

    @Override
    public void centerCamera() {
        if (presenter.updateUserLocationAndDirection()) {
            if (!centerCamera) {
                binding.getLocation.setAlpha(1f);
                centerCamera = true;
                cameraPosition = presenter.getBaseCameraPosition();
                mMap.getUiSettings().setScrollGesturesEnabled(false);
                mMap.getUiSettings().setRotateGesturesEnabled(false);
                getActivity().runOnUiThread(() -> mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition)));
            } else {
                binding.getLocation.setAlpha(0.5f);
                binding.getLocation.setPressed(false);
                centerCamera = false;
                mMap.getUiSettings().setScrollGesturesEnabled(true);
                mMap.getUiSettings().setRotateGesturesEnabled(true);
            }
        }
    }

    public void addNotification() {
        final String[] previousMessage = new String[1];
        //Probably can be done better, may not work all the time
        presenter.getNotification(new OnResult<String>() {
            @Override
            public void onSuccess(String result) {
                previousMessage[0] = result;
            }

            @Override
            public void onError(Throwable error) {

            }
        });
        makeAlertWindow(getString(R.string.add_notification), previousMessage[0], new OnResult<String>() {
            @Override
            public void onSuccess(String result) {
                if (!isNullOrEmpty(result)) {
                    Log.d("MapFragment", "Set to " + result);
                    presenter.addNotification(result.trim());
                } else {
                    Log.d("MapFragment", "Text is null or empty");
                }
            }

            @Override
            public void onError(Throwable error) {
                Log.e("MapFragment", error.getMessage());
            }
        });
    }

    //Extract somewhere (common function with FriendsFragment)
    private void makeAlertWindow(String title, String previousMessage, OnResult<String> handler) {
        AlertDialog.Builder messageAlert = new AlertDialog.Builder(getActivity());
        EditText messageEditText = new EditText(getContext());
        if (isNullOrEmpty(previousMessage)) {
            previousMessage = getString(R.string.message_field);
        }
        messageEditText.setHint(previousMessage);

        messageAlert.setTitle(title);
        messageAlert.setView(messageEditText);

        LinearLayout messageAlertLayout = new LinearLayout(getContext());
        messageAlertLayout.setOrientation(LinearLayout.VERTICAL);
        messageAlertLayout.addView(messageEditText);
        messageAlert.setView(messageAlertLayout);

        messageAlert.setPositiveButton("Continue", (dialog, whichButton) -> {
            Log.d("FriendsFragment", "value: " + messageEditText.getText().toString());
            handler.onSuccess(messageEditText.getText().toString());
        });

        messageAlert.setNegativeButton("Cancel", (dialog, whichButton) -> dialog.cancel());

        messageAlert.create().show();
    }

    public void changeMapType() {
        int mapType = mMap.getMapType() + 1;
        if (mapType > 4) {
            mapType = 0;
        }
        mMap.setMapType(mapType);
    }

    public void zoomIn() {
        currentZoom += 0.5f;
        cameraPosition = CameraPosition.builder(cameraPosition).zoom(currentZoom).build();
        getActivity().runOnUiThread(() -> mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition)));
    }

    public void zoomOut() {
        currentZoom -= 0.5f;
        cameraPosition = CameraPosition.builder(cameraPosition).zoom(currentZoom).build();
        getActivity().runOnUiThread(() -> mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition)));
    }

    @Override
    public void updateUserLocation() {
        presenter.updateUserLocationAndDirection();
        LatLng userLatLng = presenter.getUserLatLng();
        float bearing = presenter.getAzimuth();
        getActivity().runOnUiThread(() -> {
            userMarker.setPosition(userLatLng);
            userMarker.setRotation(bearing);
            if (centerCamera) {
                cameraPosition = CameraPosition.builder(cameraPosition).bearing(bearing).build();
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
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
        mMap.getUiSettings().setCompassEnabled(true);

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