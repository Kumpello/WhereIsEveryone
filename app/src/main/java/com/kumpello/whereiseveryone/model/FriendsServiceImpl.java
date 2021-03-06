package com.kumpello.whereiseveryone.model;

import static com.kumpello.whereiseveryone.utils.TextUtils.getHash;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.util.Log;

import androidx.annotation.NonNull;

import com.kumpello.whereiseveryone.R;
import com.google.firebase.database.DatabaseReference;
import com.kumpello.whereiseveryone.utils.CallbackIterator;
import com.kumpello.whereiseveryone.utils.OnResult;

import java.util.HashMap;
import java.util.Map;

public class FriendsServiceImpl implements FriendsService {

    private final DatabaseReference database;
    private final String userIDKey = "userID";
    private final String emailKey = "email";
    private final String usersKey = "users";
    private final String userFriendsKey = "userFriends";
    private final String contactsKey = "contacts";
    private final String userTypeKey = "type";
    private final String messageKey = "userMessage";
    private final String userID;
    private final SharedPreferences sharedPreferences;
    private User user;
    private final Resources resources;

    public FriendsServiceImpl(DatabaseReference databaseRef, SharedPreferences prefs, Resources resources) {
        database = databaseRef;
        sharedPreferences = prefs;
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
    public boolean addFriend(String friendsEmail, UserType userType, OnResult<String> onResult) {
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

        for (UserType type : UserType.values()) {
            findFriendID(friendsEmail, type, new OnResult<String>() {
                @Override
                public void onSuccess(String result) {
                    Log.d("FriendsService", "friendsID is " + result);
                    if (result != null) {
                        database.child(userFriendsKey).child(String.valueOf(userType)).child(userHash).child(contactsKey).child(result).setValue(true);
                        onResult.onSuccess(result);
                    }
                }

                @Override
                public void onError(Throwable error) {
                    onResult.onError(new Throwable(resources.getString(R.string.friends_id_not_found)));
                }
            });
        }

        return true;
    }

    @Override
    public void removeFriend(String friendsEmail, UserType type) {
        String email = getEmail();
        String userHash = getHash(email);

        findFriendID(friendsEmail, type, new OnResult<String>() {
            @Override
            public void onSuccess(String result) {
                database.child(userFriendsKey).child(String.valueOf(type)).child(userHash).child(contactsKey).child(result).setValue(false);
            }

            @Override
            public void onError(Throwable error) {
                Log.d("Error removing friend", userHash + " " + type);
            }
        });
    }

    @Override
    public void changeNick(String nick) {
        Log.d("FriendsService", "Nick set to " + nick);
        database.child(usersKey).child(userID).child("nick").setValue(nick);
    }

    @Override
    public void getFriendsList(@NonNull CallbackIterator<User> handler) {
        String email = getEmail();
        String userHash = getHash(email);
        String userType = getUserType();

        database.child(userFriendsKey).child(userType).child(userHash).child(contactsKey).get().addOnCompleteListener(task -> {
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

    private String getToken() {
        String userKeySharedPreferences = "userid";
        return sharedPreferences.getString(userKeySharedPreferences, "");
    }

    private String getUserType() {
        return sharedPreferences.getString(userTypeKey, "");
    }

    private String getEmail() {
        return sharedPreferences.getString(emailKey, "");
    }

    private void findFriendID(@NonNull String friendsEmail, @NonNull UserType userType,@NonNull OnResult<String> handler) {
        String friendsHash = getHash(friendsEmail);

        database.child(userFriendsKey).child(String.valueOf(userType)).child(friendsHash).child(userIDKey).get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.e("firebase", "Error getting data", task.getException());
                handler.onError(task.getException());
            } else {
                Log.d("FriendsService", "Success finding friends ID", task.getException());
                handler.onSuccess(String.valueOf(task.getResult().getValue()));
            }
        });
    }

    @Override
    public void getUserIDbyEmail(String email, UserType userType, OnResult<String> handler) {
        String emailHash = getHash(email);
        database.child(userFriendsKey).child(String.valueOf(userType)).child(emailHash).child(userIDKey).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("Adding friend, hash:", emailHash);
                handler.onSuccess((String) task.getResult().getValue());
            } else {
                handler.onError(task.getException());
            }
        });

    }

    @Override
    public void getSize(OnResult<Integer> handler) {
        String email = getEmail();
        String userHash = getHash(email);
        String userType = sharedPreferences.getString(userTypeKey, "");

        database.child(userFriendsKey).child(userType).child(userHash).child(contactsKey).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                @SuppressWarnings("unchecked")
                HashMap<String, Boolean> tempMap = (HashMap<String, Boolean>) task.getResult().getValue();
                if (tempMap != null) {
                    handler.onSuccess(tempMap.size());
                } else {
                    handler.onError(new Throwable("User has no friends"));
                }
            }
        });
    }

    public void getUser(String token, OnResult<User> handler) {
        Log.d("getUser ", token);
        database.child(usersKey).child(token).get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.e("firebase", "Error getting data", task.getException());
                handler.onError(new Throwable(resources.getString(R.string.error_getting_friend_from_database)));
            } else {
                @SuppressWarnings("unchecked")
                Map<String, String> tempMap = (HashMap<String, String>) task.getResult().getValue();
                User user = null;
                if (tempMap != null) {
                    user = new User(tempMap.get(userIDKey), tempMap.get(emailKey), UserType.valueOf(tempMap.get(userTypeKey)), tempMap.get(messageKey));
                }  else {
                    handler.onError(new Throwable("User has no friends"));
                }
                if (user != null) {
                    Log.d("User added ", user.email + " " + user.userID);
                    handler.onSuccess(user);
                } else {
                    handler.onError(new Throwable("Error getting user!"));
                }
            }
        });
    }

    private void addUserToFriendsDataBase(User user) {
        String userHash = getHash(user.email);
        Log.d("FriendsService", "userHash: " + userHash);
        String userType = sharedPreferences.getString(userTypeKey, "");

        database.child(userFriendsKey).child(userType).child(userHash).child(emailKey).setValue(user.email);
        database.child(userFriendsKey).child(userType).child(userHash).child(userIDKey).setValue(user.userID);
    }
}
