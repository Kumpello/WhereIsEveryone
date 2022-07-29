package com.kumpello.whereiseveryone.model;

import static com.kumpello.whereiseveryone.utils.TextUtils.getHash;
import static com.kumpello.whereiseveryone.utils.TextUtils.isNullOrEmpty;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
    private final String messageKey = "userMessage";
    private final String firstRunKey = "firstRun";
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
    public void addNotification(String text) {
        database.child(usersKey).child(userID).child(messageKey).setValue(text);
    }

    @Override
    public void getNotification(@NonNull OnResult<String> handler) {
        database.child(usersKey).child(userID).child(messageKey).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (task.getResult().getValue() != null) {
                    handler.onSuccess((String) task.getResult().getValue());
                } else {
                    handler.onSuccess(null);
                }
            } else {
                handler.onError(task.getException());
                Log.e("firebase", "Error getting data", task.getException());
            }
        });
    }

    @Override
    public void saveToken(String token) {
        userID = token;
        myEdit.putString(userKeySharedPreferences, token);
        myEdit.commit();
        Log.d("UserService", "Token saved " + token);
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
        myEdit.commit();
        database.child(usersKey).child(userID).child(userTypeKey).setValue(type);
    }

    @Override
    @NonNull
    public String getToken() {
        String token = sharedPreferences.getString(userKeySharedPreferences, "");
        Log.d("Getting token", token);
        return token;
    }

    @NonNull
    public String getEmail() {
        String email = sharedPreferences.getString(emailKey, "");
        Log.d("UserService", "Got email: " + email);
        return email;
    }

    @Override
    public UserType getUserType() {
        UserType userType = null;
        try {
            userType = UserType.valueOf(sharedPreferences.getString(userTypeKey, ""));    
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("Error getting User Type", "sharedPreferences" + sharedPreferences.getString(userTypeKey, ""));
        }
        
        return userType; 
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
        String userType = String.valueOf(user.type);
        //Checked in friends
        database.child(userFriendsKey).child(userType).child(hashedMail).child(contactsKey).child(userID).get().addOnCompleteListener(task -> {
            Log.d("Checking friendship", String.valueOf(task.getResult().getValue()));
            result.onSuccess((Boolean) task.getResult().getValue());
        });
    }

    @Override
    public void getFriendsList(@NonNull CallbackIterator<User> handler) {
        String userHash = getHash(email);
        String userType = getUserType().toString();

        Log.d("User Service:Getting friend list, fetching data", userHash + " " + userType);
        database.child(userFriendsKey).child(userType).child(userHash).child(contactsKey).get().addOnCompleteListener(task -> {
            Log.d("User Service:Getting friend list, task completed", String.valueOf(task.getException()));
            if (task.isSuccessful()) {
                if (task.getResult().getValue() != null) {
                    @SuppressWarnings("unchecked")
                    HashMap<String, Boolean> tempMap = (HashMap<String, Boolean>) task.getResult().getValue();
                    for (Map.Entry<String, Boolean> p : tempMap.entrySet()) {
                        Log.d("User Service:Getting friend list, friend key", p.getKey());
                        getUser(p.getKey(), new OnResult<User>() {
                            @Override
                            public void onSuccess(User result) {
                                Log.d("User Service:Adding friend ", result.email);
                                handler.onNext(result);
                            }

                            @Override
                            public void onError(Throwable error) {
                                Log.d("User Service:Error adding friend ", error.getMessage());
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
                        User user = new User((String) tempMap.get(userIDKey), (String) tempMap.get(emailKey), (String) tempMap.get(userTypeKey));
                        Log.d("User added ", user.email + " " + user.userID);
                        user.nick = (String) Objects.requireNonNull(tempMap.get(nickKey));
                        user.message = (String) tempMap.get(messageKey);
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

    @Override
    public void firstRun(Boolean value) {
        myEdit.putString(firstRunKey, String.valueOf(value));
        myEdit.commit();
        Log.d("UserService", "first run");
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean userExists() {
        // Boolean value that is false when account is created and set true on first user upload
        String firstRun = sharedPreferences.getString(firstRunKey, "");
        return firstRun.equals(String.valueOf(true));
    }


}
