package com.example.whereiseveryone.model;

import static com.example.whereiseveryone.utils.TextUtils.getHash;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.whereiseveryone.R;
import com.example.whereiseveryone.utils.CallbackIterator;
import com.example.whereiseveryone.utils.OnResult;
import com.google.firebase.database.DatabaseReference;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class UserServiceImpl implements UserService {

    private final String userKeySharedPreferences = "userid";
    private final String emailKey = "email";
    private final String userKey = "userID";
    private final String nickKey = "nick";
    private final String locationKey = "userLocation";
    private final String latitude = "latitude";
    private final String longitude = "longitude";
    private final String azimuthKey = "userAzimuth";
    private String userID;
    private String email;
    private final SharedPreferences sharedPreferences;
    private final SharedPreferences.Editor myEdit;
    private final DatabaseReference database;
    private final Resources resources;

    public UserServiceImpl(DatabaseReference reference, SharedPreferences preferences, Resources resources) {
        database = reference;
        sharedPreferences = preferences;
        myEdit = sharedPreferences.edit();
        this.resources = resources;
        userID = getToken();
        email = getEmail();
    }

    @Override
    public void saveToken(@NonNull String token) {
        userID = token;
        myEdit.putString(userKeySharedPreferences, token);
        myEdit.commit();
    }

    @Override
    public void saveEmail(@NonNull String email) {
        this.email = email;
        myEdit.putString(emailKey, email);
        myEdit.commit();
        Log.d("UserService", "Email saved: " + email);
    }

    @Override
    @NonNull
    public String getToken() {
        return sharedPreferences.getString(userKeySharedPreferences, "");
    }

    @NonNull
    public String getEmail() {
        String email = sharedPreferences.getString(emailKey, "");
        Log.d("UserService", "Got email: " + email);
        return email;
    }

    @Override
    public void updateUserOnServer(User user) {
        database.child("users").child(userID).setValue(user);
    }

    @Override
    public void updateUserLocationAndDirection(User user) {
        database.child("users").child(userID).child("userAzimuth").setValue(user.userAzimuth);
        database.child("users").child(userID).child("userLocation").setValue(user.userLocation);
    }

    public void checkFriendship(User user, OnResult<Boolean> result) {
        String hashedMail = getHash(user.email);
        String friendID = user.userID;

        database.child("userFriends").child(hashedMail).child("contacts").child(friendID).get().addOnCompleteListener(task -> {
            result.onSuccess((Boolean) task.getResult().getValue());
        });
    }

    @Override
    public void getFriendsList(@NonNull CallbackIterator<User> handler) {
        String userHash = getHash(email);

        database.child("userFriends").child(userHash).child("contacts").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (task.getResult().getValue() != null) {
                    HashMap<String, Boolean> tempMap = (HashMap<String, Boolean>) task.getResult().getValue();
                    for (Map.Entry<String, Boolean> p : tempMap.entrySet()) {
                        Log.d("Getting friend list, friend key", p.getKey());
                        getUser(p.getKey(), new OnResult<User>() {
                            @Override
                            public void onSuccess(User result) {
                                Log.d("Adding friend ", result.email);
                                handler.onNext(result);
                            }

                            @Override
                            public void onError(Throwable error) {
                                //ToDO
                            }
                        });
                    }
                }
            } else {
                Log.e("firebase", "Error getting data", task.getException());
            }

        });
    }

    public void getUser(String token, OnResult<User> handler) {
        Log.d("getUser ", token);
        database.child("users").child(token).get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.e("firebase", "Error getting data", task.getException());
                handler.onError(new Throwable(resources.getString(R.string.error_getting_friend_from_database)));
            } else {
                Map<String, String> tempMap = (HashMap<String, String>) task.getResult().getValue();
                User user = new User(tempMap.get(userKey), tempMap.get(emailKey));
                Log.d("User added ", user.email + " " + user.userID);
                user.nick = tempMap.get(nickKey);
                //user.userLocation = tempMap.get(locationKey);
                user.userAzimuth = Float.valueOf(tempMap.get(azimuthKey));
                handler.onSuccess(user);
            }
        });
    }

    @Override
    public boolean userExists() {
        AtomicBoolean userExists = new AtomicBoolean(false);
        database.child("users").child(userID).get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.e("firebase", "Error getting data", task.getException());
            } else {
                Map<String, String> tempMap = (HashMap<String, String>) task.getResult().getValue();
                if (tempMap != null) {
                    userExists.set(true);
                    Log.d("userService", "user exists");
                } else {
                    userExists.set(false);
                    Log.d("userService", "user doesn't exist");
                }
            }
        });

        return userExists.get();
    }


}
