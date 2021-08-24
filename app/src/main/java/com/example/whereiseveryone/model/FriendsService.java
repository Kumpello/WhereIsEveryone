package com.example.whereiseveryone.model;

import java.util.List;

public interface FriendsService {
    boolean addFriend(String email);
    void removeFriend(String email);
    void changeNick(String nick);
    List<User> getFriendsList();
}
