package com.example.whereiseveryone.model;

import static com.example.whereiseveryone.utils.TextUtils.getHash;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.whereiseveryone.R;
import com.example.whereiseveryone.utils.OnResult;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FriendsServiceImpl implements FriendsService {

    private final DatabaseReference database;
    private final String userKey = "userID";
    private final String userKeySharedPreferences = "userid";
    private final String emailKey = "email";
    private final String userID;
    private SharedPreferences sharedPreferences;
    private final SharedPreferences.Editor sharedPreferencesEdit;
    private User user;
    private final Resources resources;

    public FriendsServiceImpl(DatabaseReference databaseRef, SharedPreferences prefs, Resources resources) {
        database = databaseRef;
        sharedPreferences = prefs;
        sharedPreferencesEdit = sharedPreferences.edit();
        this.resources = resources;
        userID = getToken();
        getUser(userID, new OnResult<User>() {

            @Override
            public void onSuccess(User result) {
                user = result;
                addUserToFriendsDataBase(user);
            }

            @Override
            public void onError(Throwable error) {

            }
        });
    }

    // async method - return true/false just basing on a validation
    @Override
    public boolean addFriend(String friendsEmail, OnResult<String> onResult) {
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        if (friendsEmail.isEmpty()) {
            onResult.onError(new Throwable(resources.getString(R.string.text_is_empty)));
            return false;
        }
        if (!friendsEmail.matches(emailPattern)) {
            onResult.onError(new Throwable(resources.getString(R.string.text_is_not_email)));
            return false;
        }

        String email = getEmail();
        String userHash = getHash(email);

        findFriendID(friendsEmail, new OnResult<String>() {
            @Override
            public void onSuccess(String result) {
                Log.d("FriendsService", "friendsID is " + result);
                if (result != null) {
                    database.child("userFriends").child(userHash).child("contacts").child(result).setValue(true);
                }
                onResult.onSuccess(result);
            }

            @Override
            public void onError(Throwable error) {
                onResult.onError(new Throwable(resources.getString(R.string.friends_id_not_found)));
            }
        });
        return true;
    }

    @Override
    public void removeFriend(String friendsEmail) {
        String email = getEmail();
        String userHash = getHash(email);

        findFriendID(friendsEmail, null);

        // TODO
        // database.child("userFriends").child(userHash).child("contacts").child(friendsID).setValue(false);
    }

    @Override
    public void changeNick(String nick) {
        Log.d("FriendsService", "Nick set to " + nick);
        database.child("users").child(userID).child("nick").setValue(nick);
    }

    @Override
    public void getFriendsList(@NonNull OnResult<List<User>> handler) {
        ArrayList<User> userList = new ArrayList<>();
        String email = getEmail();
        String userHash = getHash(email);

        database.child("userFriends").child(userHash).child("contacts").get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.e("firebase", "Error getting data", task.getException());
            } else {
                if (task.getResult().getValue() != null) {
                    HashMap<String, Boolean> tempMap = (HashMap<String, Boolean>) task.getResult().getValue();
                    for (Map.Entry<String, Boolean> p : tempMap.entrySet()) {
                        Log.d("Getting friend list, friend key", p.getKey());
                        getUser(p.getKey(), new OnResult<User>() {
                            @Override
                            public void onSuccess(User result) {
                                Log.d("Adding friend ", result.email);
                                userList.add(result);
                            }

                            @Override
                            public void onError(Throwable error) {
                                //ToDO
                            }
                        });
                        handler.onSuccess(userList);
                    }
                }
                //Log.d("firebase", String.valueOf(task.getResult().getValue()));

            }
        });
    }

    private String getToken() {
        return sharedPreferences.getString(userKeySharedPreferences, "");
    }

    private String getEmail() {
        return sharedPreferences.getString(emailKey, "");
    }

    private void findFriendID(@NonNull String friendsEmail, @NonNull OnResult<String> handler) {
        String friendsHash = getHash(friendsEmail);

        database.child("userFriends").child(friendsHash).child(userKey).get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.e("firebase", "Error getting data", task.getException());
                handler.onError(task.getException());
            } else {
                handler.onSuccess(String.valueOf(task.getResult().getValue()));
            }
        });
    }

    public void getUserIDbyEmail(String email, OnResult<String> handler) {
        String emailHash = getHash(email);
        database.child("userFriends").child(emailHash).child(userKey).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("Adding friend, hash:", emailHash);
                handler.onSuccess((String) task.getResult().getValue());
            } else {
                handler.onError(task.getException());
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
                handler.onSuccess(user);
            }
        });
    }

    private void addUserToFriendsDataBase(User user) {
        String userHash = getHash(user.email);
        Log.d("FriendsService", "userHash: " + userHash);
        database.child("userFriends").child(userHash).child(emailKey).setValue(user.email);
        database.child("userFriends").child(userHash).child(userKey).setValue(user.userID);
    }
}
