package com.example.whereiseveryone.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

public interface UserService {
    // null for log out
    void saveToken(@Nullable String token);

    void saveEmail(@NonNull String email);

    @NotNull
    String getEmail();

    // returns user-token or null if there is no user logged in
    @NotNull
    String getToken();

    void updateUserOnServer(User user);
}
