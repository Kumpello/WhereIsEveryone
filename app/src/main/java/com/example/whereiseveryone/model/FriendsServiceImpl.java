package com.example.whereiseveryone.model;

import static android.content.Context.MODE_PRIVATE;
import static com.example.whereiseveryone.utils.TextUtils.cutSpecialSigns;
import static com.example.whereiseveryone.utils.TextUtils.isNullOrEmpty;

import android.app.Activity;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.whereiseveryone.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FriendsServiceImpl implements FriendsService {

    private FirebaseDatabase database;
    private DatabaseReference mDatabase;
    private final String userKey = "userid";
    private String userID;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor myEdit;
    private Activity activity;
    private User user;

    public FriendsServiceImpl(Activity activity) {
        this.activity = activity;
        database = FirebaseDatabase.getInstance(activity.getString(R.string.server_address));
        mDatabase = FirebaseDatabase.getInstance().getReference();
        sharedPreferences = activity.getSharedPreferences("WhereIsEveryone", MODE_PRIVATE);
        myEdit = sharedPreferences.edit();
        userID = getToken();
        user = getUser(userID);
    }

    @Override
    public boolean addFriend(String email) {
        String token = getToken();
        String userEmail = user.email;
        String friendsID = findFriendsID(email);

        if (isNullOrEmpty(friendsID)){
            return false;
        } else {
            mDatabase.child("userFriends").child(token).child("email").setValue(userEmail);
            mDatabase.child("userFriends").child(token).child("contacts").child(friendsID).setValue(true);

            return true;
        }


    }

    @Override
    public void removeFriend(String email) {
        String token = getToken();
        String friendsID = findFriendsID(email);

        mDatabase.child("userFriends").child(token).child("contacts").child(friendsID).setValue(false);
    }

    @Override
    public void changeNick(String nick) {
        mDatabase.child("users").child(getToken()).child("nick").setValue(nick);
    }

    @Override
    public List<User> getFriendsList() {
        ArrayList<String> friendsList = new ArrayList<>();
        String token = getToken();

        mDatabase.child("userFriends").child(token).child("contacts").get().addOnCompleteListener((OnCompleteListener<DataSnapshot>) task -> {
            if (!task.isSuccessful()) {
                Log.e("firebase", "Error getting data", task.getException());
            } else {
                //Log.d("firebase", String.valueOf(task.getResult().getValue()));
                HashMap<String, Boolean> tempMap = (HashMap<String, Boolean>) task.getResult().getValue();
                for (Map.Entry<String, Boolean> p : tempMap.entrySet()) {
                    friendsList.add(p.getKey());
                }
            }
        });

        ArrayList<User> userList = new ArrayList<User>();

        return Collections.emptyList();
    }

    private String getToken() {
        return sharedPreferences.getString(userKey, "");
    }

    private String findFriendsID(String email) {
        String userEmailWithoutSpecialSigns = cutSpecialSigns(email);
        final String[] friendsID = new String[1];

        mDatabase.child("userEmails").child(userEmailWithoutSpecialSigns).child(userKey).get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.e("firebase", "Error getting data", task.getException());
            }
            else {
                Log.d("firebase", String.valueOf(task.getResult().getValue()));
                friendsID[0] = String.valueOf(task.getResult().getValue());
            }
        });

        return friendsID[0];
    }

    private User getUser(String token) {
        final User[] tempUser = new User[1];
        mDatabase.child("users").child(token).get().addOnCompleteListener((OnCompleteListener<DataSnapshot>) task -> {
            if (!task.isSuccessful()) {
                Log.e("firebase", "Error getting data", task.getException());
            } else {
                //Log.d("firebase", String.valueOf(task.getResult().getValue()));
                Map<String, String> tempMap = (HashMap<String, String>) task.getResult().getValue();
                tempUser[0] = new User(tempMap.get("email"));
                tempUser[0].nick = tempMap.get("nick");
            }
        });
        return tempUser[0];
    }
}
