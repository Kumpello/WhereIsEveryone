package com.example.WhereIsEveryone.model;

import androidx.annotation.Nullable;

public class UserServiceImpl implements UserService {

    // TODO: Keep the token in SharedPreferences

    private String userID;

    public UserServiceImpl() {
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
