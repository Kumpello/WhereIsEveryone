package com.example.whereiseveryone.model;

import com.google.android.gms.maps.model.LatLng;

public class User {

    public String email;
    public LatLng userLocation;
    public float userAzimuth;
    public String nick;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String email) {
        this.email = email;
        this.userLocation = null;
        this.userAzimuth = 0;
        this.nick = null;
    }
}
