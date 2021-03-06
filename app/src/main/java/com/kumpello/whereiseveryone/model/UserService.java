package com.kumpello.whereiseveryone.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;
import com.kumpello.whereiseveryone.utils.CallbackIterator;
import com.kumpello.whereiseveryone.utils.OnResult;

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

    @NonNull
    UserType getUserType();

    void updateUserOnServer(User user);

    void updateUserLocationAndDirection(User user);

    boolean userExists();

    void addNotification(String text);

    void getNotification(@NonNull OnResult<String> handler);

    void getFriendsList(CallbackIterator<User> userList);

    void updateFriendsList(List<User> friends, CallbackIterator<User> userList);

    void checkFriendship(User user, OnResult<Boolean> result);

    void firstRun(Boolean value);

    void saveLoginType(UserType type);

    void savePosition(LatLng latLng);

    LatLng getLastPosition();

    void setUserPlaced(Boolean placed);

    boolean getUserPlaced();
}
