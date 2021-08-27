package com.example.whereiseveryone.presenter;

import com.example.whereiseveryone.model.FriendsService;
import com.example.whereiseveryone.model.User;
import com.example.whereiseveryone.mvp.BasePresenter;
import com.example.whereiseveryone.view.FriendsView;

import java.util.List;

public class FriendsPresenterImpl extends BasePresenter<FriendsView> implements FriendsPresenter{

    FriendsService friendsService;

    public FriendsPresenterImpl(FriendsService friendsService) {
        this.friendsService = friendsService;
    }

    @Override
    public boolean addFriend(String email) {
        return friendsService.addFriend(email);
    }

    @Override
    public void removeFriend(String email) {
        friendsService.removeFriend(email);
        view.removeFriend(email);
    }

    @Override
    public void changeNick(String nick) {
        friendsService.changeNick(nick);
    }

    @Override
    public List<User> getFriendsList() {
        return friendsService.getFriendsList();
    }
}
