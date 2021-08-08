package com.example.whereiseveryone.model;

import android.content.SharedPreferences;

import androidx.annotation.Nullable;

public interface UserService {
    // null for log out
    void saveToken(@Nullable String token);

    // returns user-token or null if there is no user logged in
    @Nullable
    String getToken();

    void passSharedPreferences(SharedPreferences sharedPreferences);
}
