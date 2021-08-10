package com.example.whereiseveryone.model;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.content.SharedPreferences;

import androidx.annotation.Nullable;

import com.example.whereiseveryone.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UserServiceImpl implements UserService {

    private final String userKey = "userid";
    private String userID;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor myEdit;
    private FirebaseDatabase database;
    private DatabaseReference mDatabase;

    public UserServiceImpl(Activity activity) {
        database = FirebaseDatabase.getInstance(String.valueOf(R.string.server_address));
        mDatabase = FirebaseDatabase.getInstance().getReference();
        sharedPreferences = activity.getSharedPreferences("WhereIsEveryone",MODE_PRIVATE);
        this.userID = null;
    }

    @Override
    public void saveToken(@Nullable String token) {
        userID = token;
        myEdit.putString(userKey, userID);
        myEdit.commit();
        myEdit.apply();
    }

    @Override
    @Nullable
    public String getToken() {
        return sharedPreferences.getString(userKey, "");
    }

    @Override
    public void passSharedPreferences(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
        myEdit = sharedPreferences.edit();
    }

    @Override
    public void updateUserOnServer(User user) {
        mDatabase.child("users").child(user.email).setValue(user);
    }
}
