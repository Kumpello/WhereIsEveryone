package com.example.whereiseveryone.model;

import com.example.whereiseveryone.utils.CallbackIterator;
import com.example.whereiseveryone.utils.OnResult;

public interface FriendsService {
    boolean addFriend(String email, OnResult<String> handler);

    // TODO: Do this asynchronous
    void removeFriend(String email);

    void changeNick(String nick);

    void getFriendsList(CallbackIterator<User> userList);

    void getUser(String token, OnResult<User> handler);

    void getUserIDbyEmail(String email, OnResult<String> handler);

    void getSize(OnResult<Integer> handler);
}
