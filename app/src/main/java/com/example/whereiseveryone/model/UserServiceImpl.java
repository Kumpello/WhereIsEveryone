package com.example.whereiseveryone.model;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;

import androidx.annotation.Nullable;

public class UserServiceImpl implements UserService {


    private String userID;

    public UserServiceImpl() {
        SharedPreferences sharedPreferences = getSharedPreferences("WhereIsEveryone",MODE_PRIVATE);
        this.userID = null;
    }

    @Override
    public void saveToken(@Nullable String token) {
        userID = token;
    }

    @Override
    @Nullable
    public String getToken() {
        return userID;
    }
}
