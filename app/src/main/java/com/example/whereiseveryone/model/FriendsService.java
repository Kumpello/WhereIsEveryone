package com.example.whereiseveryone.model;

import com.example.whereiseveryone.utils.OnResult;

import java.util.List;

public interface FriendsService {
    boolean addFriend(String email, OnResult<String> handler);

    // TODO: Do this asynchronous
    void removeFriend(String email);

    void changeNick(String nick);

    List<User> getFriendsList();
}
