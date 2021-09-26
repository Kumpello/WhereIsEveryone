package com.example.whereiseveryone.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;

import java.util.Objects;

@SuppressWarnings("all")

public class User {



    @NonNull
    public String email;
    @Nullable
    public LatLng userLocation;
    @Nullable
    public float userAzimuth;
    @NonNull
    public String nick;
    @Nullable
    public String userID;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String userID) {
        this.userID = userID;
        this.email = null;
        this.userLocation = null;
        this.userAzimuth = 0;
        this.nick = "NoName";
    }

    public User(String userID, String email) {
        this.userID = userID;
        this.email = email;
        this.userLocation = null;
        this.userAzimuth = 0;
        this.nick = "NoName";
    }

    @Override
    public String toString(){
        return userID + email + userLocation + userAzimuth + nick;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return Float.compare(user.userAzimuth, userAzimuth) == 0 && email.equals(user.email) && Objects.equals(userLocation, user.userLocation) && nick.equals(user.nick);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email, userLocation, userAzimuth, nick);
    }
}
