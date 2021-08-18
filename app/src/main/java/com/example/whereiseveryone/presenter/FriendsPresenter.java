package com.example.whereiseveryone.presenter;

import com.example.whereiseveryone.mvp.Contract;
import com.example.whereiseveryone.view.FriendsView;

public interface FriendsPresenter extends Contract.Presenter<FriendsView> {
    void addFriend();
    void removeFriend(String email);
}
