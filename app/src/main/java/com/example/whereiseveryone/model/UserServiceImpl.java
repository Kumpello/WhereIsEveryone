package com.example.whereiseveryone.model;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.whereiseveryone.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class UserServiceImpl implements UserService {

    private final String userKeySharedPreferences = "userid";
    private final String emailKey = "email";
    private String userID;
    private String email;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor myEdit;
    private FirebaseDatabase database;
    private DatabaseReference mDatabase;

    public UserServiceImpl(Activity activity) {
        database = FirebaseDatabase.getInstance(activity.getString(R.string.server_address));
        mDatabase = database.getReference();
        sharedPreferences = activity.getSharedPreferences("WhereIsEveryone",MODE_PRIVATE);
        myEdit = sharedPreferences.edit();
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
        mDatabase.child("users").child(userID).setValue(user);
    }

    @Override
    public void updateUserLocationAndDirection(User user) {
        mDatabase.child("users").child(userID).child("userAzimuth").setValue(user.userAzimuth);
        mDatabase.child("users").child(userID).child("userLocation").setValue(user.userLocation);
    }

    @Override
    public boolean userExists() {
        AtomicBoolean userExists = new AtomicBoolean(false);
        mDatabase.child("users").child(userID).get().addOnCompleteListener(task -> {
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
