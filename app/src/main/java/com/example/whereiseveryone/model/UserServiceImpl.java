package com.example.whereiseveryone.model;

import android.content.SharedPreferences;

import androidx.annotation.Nullable;

public class UserServiceImpl implements UserService {

    private final String userKey = "userid";
    private String userID;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor myEdit;

    public UserServiceImpl() {
        this.userID = null;
    }

    @Override
    public void saveToken(@Nullable String token) {
        userID = token;
        myEdit.putString(userKey, userID);
        myEdit.commit();
    }

    @Override
    @Nullable
    public String getToken() {
        return sharedPreferences.getString(userKey, "");
    }

    @Override
    public void passSharedPreferences(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
        myEdit = sharedPreferences.edit();
    }

}
