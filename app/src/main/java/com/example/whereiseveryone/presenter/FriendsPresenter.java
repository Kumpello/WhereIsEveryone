package com.example.whereiseveryone.presenter;

import com.example.whereiseveryone.model.User;
import com.example.whereiseveryone.mvp.Contract;
import com.example.whereiseveryone.utils.OnResult;
import com.example.whereiseveryone.view.FriendsView;

import java.util.List;

public interface FriendsPresenter extends Contract.Presenter<FriendsView> {
    boolean addFriend(String email);
    void removeFriend(String email);
    void changeNick(String nick);
    void getFriendsList();
}
