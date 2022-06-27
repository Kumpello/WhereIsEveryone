package com.kumpello.whereiseveryone.model;

import static com.kumpello.whereiseveryone.utils.TextUtils.getHash;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DatabaseReference;
import com.kumpello.whereiseveryone.R;
import com.kumpello.whereiseveryone.utils.CallbackIterator;
import com.kumpello.whereiseveryone.utils.OnResult;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

public class UserServiceImpl implements UserService {

    private final String userKeySharedPreferences = "userid";
    private final String userTypeKey = "type";
    private final String emailKey = "email";
    private final String userIDKey = "userID";
    private final String nickKey = "nick";
    private final String usersKey = "users";
    private final String userFriendsKey = "userFriends";
    private final String contactsKey = "contacts";
    private final String locationKey = "userLocation";
    private final String latitudeKey = "latitude";
    private final String longitudeKey = "longitude";
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
    public void saveToken(String token) {
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
    public void saveLoginType(UserType type) {
        myEdit.putString(userTypeKey, String.valueOf(type));
        database.child(usersKey).child(userID).child(userTypeKey).setValue(type);
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
    public UserType getUserType() {
        return UserType.valueOf(sharedPreferences.getString(userTypeKey, ""));
    }

    @Override
    public void updateUserOnServer(User user) {
        database.child(usersKey).child(userID).setValue(user);
    }
    @Override
    public void updateUserLocationAndDirection(User user) {
        database.child(usersKey).child(userID).child("userAzimuth").setValue(user.userAzimuth);
        database.child(usersKey).child(userID).child("userLocation").setValue(user.userLocation);
    }

    public void checkFriendship(User user, OnResult<Boolean> result) {
        String hashedMail = getHash(user.email);

        database.child(userFriendsKey).child(hashedMail).child(contactsKey).child(userID).get().addOnCompleteListener(task -> {
            Log.d("Checking friendship", String.valueOf(task.getResult().getValue()));
            result.onSuccess((Boolean) task.getResult().getValue());
        });
    }

    @Override
    public void getFriendsList(@NonNull CallbackIterator<User> handler) {
        String userHash = getHash(email);

        database.child(userFriendsKey).child(userHash).child(contactsKey).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (task.getResult().getValue() != null) {
                    @SuppressWarnings("unchecked")
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

    public void updateFriendsList(List<User> friends, @NonNull CallbackIterator<User> handler) {
        for (User user : friends) {
            getUser(user.userID, new OnResult<User>() {
                @Override
                public void onSuccess(User result) {
                    handler.onNext(result);
                }

                @Override
                public void onError(Throwable error) {
                    //Todo
                }
            });
        }
    }

    @SuppressWarnings("all")
    public void getUser(String token, OnResult<User> handler) {
        Log.d("getUser ", token);
        database.child(usersKey).child(token).get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.e("firebase", "Error getting data", task.getException());
                handler.onError(new Throwable(resources.getString(R.string.error_getting_friend_from_database)));
            } else {
                try {
                    Map<String, Object> tempMap = (HashMap<String, Object>) task.getResult().getValue();
                    if (tempMap != null) {
                        User user = new User((String) tempMap.get(userIDKey), (String) tempMap.get(emailKey), (UserType) tempMap.get(userTypeKey));
                        Log.d("User added ", user.email + " " + user.userID);
                        user.nick = (String) Objects.requireNonNull(tempMap.get(nickKey));
                        HashMap<String, Double> latLng = (HashMap<String, Double>) tempMap.get(locationKey);
                        user.userLocation = new LatLng(latLng.get(latitudeKey), latLng.get(longitudeKey));
                        Double userAzimuth = (Double) tempMap.get(azimuthKey);
                        user.userAzimuth =  userAzimuth.floatValue();
                        handler.onSuccess(user);
                    } else {
                        handler.onError(new Exception("Error getting user!"));
                    }
                } catch (Exception e) {
                    handler.onError(e);
                }
            }
        });
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean userExists() {
        AtomicBoolean userExists = new AtomicBoolean(false);
        database.child(usersKey).child(userID).get().addOnCompleteListener(task -> {
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
