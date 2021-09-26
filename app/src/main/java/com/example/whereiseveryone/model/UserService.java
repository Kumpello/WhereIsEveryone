package com.example.whereiseveryone.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.whereiseveryone.utils.CallbackIterator;
import com.example.whereiseveryone.utils.OnResult;

import org.jetbrains.annotations.NotNull;

import java.util.List;

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

    void updateUserLocationAndDirection(User user);

    boolean userExists();

    void getFriendsList(CallbackIterator<User> userList);

    void updateFriendsList(List<User> friends, CallbackIterator<User> userList);

    void checkFriendship(User user, OnResult<Boolean> result);
}
