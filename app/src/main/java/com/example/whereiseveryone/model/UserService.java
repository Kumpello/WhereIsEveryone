package com.example.whereiseveryone.model;

import androidx.annotation.Nullable;

public interface UserService {
    // null for log out
    void saveToken(@Nullable String token);

    // returns user-token or null if there is no user logged in
    @Nullable
    String getToken();
}
