package com.example.whereiseveryone.view;

import com.example.whereiseveryone.mvp.Contract;

public interface FriendsView extends Contract.View{
    void addFriend();
    void removeFriend(String email);
    void changeNick();
}
