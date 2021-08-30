package com.example.whereiseveryone.model;

import static android.content.Context.MODE_PRIVATE;
import static com.example.whereiseveryone.utils.TextUtils.cutSpecialSigns;
import static com.example.whereiseveryone.utils.TextUtils.isNullOrEmpty;

import android.app.Activity;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.example.whereiseveryone.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class FriendsServiceImpl implements FriendsService {

    private final FirebaseDatabase database;
    private final DatabaseReference mDatabase;
    private final String userKey = "userid";
    private final String emailKey = "email";
    private final String userID;
    private SharedPreferences sharedPreferences;
    private final SharedPreferences.Editor myEdit;
    private final Activity activity;
    private final User user;

    public FriendsServiceImpl(Activity activity) {
        this.activity = activity;
        database = FirebaseDatabase.getInstance(activity.getString(R.string.server_address));
        mDatabase = database.getReference();
        sharedPreferences = activity.getSharedPreferences("WhereIsEveryone", MODE_PRIVATE);
        myEdit = sharedPreferences.edit();
        userID = getToken();
        user = getUser(userID);
    }

    @Override
    public boolean addFriend(String email) {
        String userEmailWithoutSpecialSigns = cutSpecialSigns(user.email);
        String friendsID = findFriendsID(email);

        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        if (email.isEmpty()) {
            Toast.makeText(activity.getApplicationContext(), "enter email address", Toast.LENGTH_SHORT).show();
        } else {
            if (email.trim().matches(emailPattern)) {
                Toast.makeText(activity.getApplicationContext(), "valid email address", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(activity.getApplicationContext(), "Invalid email address", Toast.LENGTH_SHORT).show();
            }
        }

        if (isNullOrEmpty(friendsID)) {
            return false;
        } else {
            mDatabase.child("userFriends").child(userEmailWithoutSpecialSigns).child("contacts").child(friendsID).setValue(true);
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
        Log.d("FriendsService", "Nick set to " + nick);
        mDatabase.child("users").child(userID).child("nick").setValue(nick);
    }

    @Override
    public List<User> getFriendsList() {
        ArrayList<String> friendsList = new ArrayList<>();
        String token = getToken();

        mDatabase.child("userFriends").child(token).child("contacts").get().addOnCompleteListener((OnCompleteListener<DataSnapshot>) task -> {
            if (!task.isSuccessful()) {
                Log.e("firebase", "Error getting data", task.getException());
            } else {
                if (task.getResult().getValue() != null) {
                    HashMap<String, Boolean> tempMap = (HashMap<String, Boolean>) task.getResult().getValue();
                    for (Map.Entry<String, Boolean> p : tempMap.entrySet()) {
                        friendsList.add(p.getKey());
                    }
                }
                //Log.d("firebase", String.valueOf(task.getResult().getValue()));

            }
        });

        ArrayList<User> userList = new ArrayList<>();
        friendsList.forEach(currentUserID -> userList.add(getUser(currentUserID)));

        return userList;
    }

    private String getToken() {
        return sharedPreferences.getString(userKey, "");
    }

    private String findFriendsID(String email) {
        String userEmailWithoutSpecialSigns = cutSpecialSigns(email);
        final String[] friendsID = new String[1];

        mDatabase.child("userFriends").child(userEmailWithoutSpecialSigns).child(userKey).get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.e("firebase", "Error getting data", task.getException());
            } else {
                Log.d("firebase", String.valueOf(task.getResult().getValue()));
                friendsID[0] = String.valueOf(task.getResult().getValue());
            }
        });

        return friendsID[0];
    }

    private User getUser(String token) {
        final boolean[] firstRun = {true};
        final User[] tempUser = new User[1];
        mDatabase.child("users").child(token).get().addOnCompleteListener((OnCompleteListener<DataSnapshot>) task -> {
            if (!task.isSuccessful()) {
                Log.e("firebase", "Error getting data", task.getException());
            } else {
                Map<String, String> tempMap = (HashMap<String, String>) task.getResult().getValue();
                tempUser[0] = new User(tempMap.get(userKey), tempMap.get(emailKey));
                tempUser[0].nick = Objects.requireNonNull(tempMap.get("nick"));
                firstRun[0] = false;
                Log.d("firebase", tempUser[0].userID + " " + tempUser[0].email);
                if (firstRun[0]) {
                    addUserToFriendsDataBase(tempUser[0]);
                }
            }
        });
        return tempUser[0];
    }

    private void addUserToFriendsDataBase(User user) {
        String userEmailWithoutSpecialSigns = cutSpecialSigns(user.email);
        Log.d("FriendsService", "userEmailWithoutSpecialSigns: " + userEmailWithoutSpecialSigns);
        mDatabase.child("userFriends").child(userEmailWithoutSpecialSigns).child("email").setValue(user.email);
        mDatabase.child("userFriends").child(userEmailWithoutSpecialSigns).child(userKey).setValue(user.userID);
    }
}
