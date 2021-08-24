package com.example.whereiseveryone.model;

import static android.content.Context.MODE_PRIVATE;

import static com.example.whereiseveryone.utils.TextUtils.cutSpecialSigns;

import android.app.Activity;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
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
        database = FirebaseDatabase.getInstance(activity.getString(R.string.server_address));
        mDatabase = FirebaseDatabase.getInstance().getReference();
        sharedPreferences = activity.getSharedPreferences("WhereIsEveryone",MODE_PRIVATE);
        myEdit = sharedPreferences.edit();
        this.userID = getToken();
    }

    @Override
    public void saveToken(@NonNull String token) {
        userID = token;
        myEdit.putString(userKey, userID);
        myEdit.commit();
        //myEdit.apply();
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
        mDatabase.child("users").child(getToken()).setValue(user);
        String userEmailWithoutSpecialSigns = cutSpecialSigns(user.email);
        //ToDO
        //Add checkup if email exists
        mDatabase.child("userEmails").child(userEmailWithoutSpecialSigns).child(userKey).setValue(userID);
    }
}
