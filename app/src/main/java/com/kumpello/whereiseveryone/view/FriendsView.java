package com.kumpello.whereiseveryone.view;

import com.kumpello.whereiseveryone.model.User;
import com.kumpello.whereiseveryone.mvp.Contract;

public interface FriendsView extends Contract.View{
    void addFriend();
    void addFriendToAdapter(User user);
    void removeFriend(String email);
    void changeNick();
}
