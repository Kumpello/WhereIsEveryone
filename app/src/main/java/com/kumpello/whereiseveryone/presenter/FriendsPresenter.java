package com.kumpello.whereiseveryone.presenter;

import com.kumpello.whereiseveryone.model.UserType;
import com.kumpello.whereiseveryone.mvp.Contract;
import com.kumpello.whereiseveryone.view.FriendsView;

public interface FriendsPresenter extends Contract.Presenter<FriendsView> {
    boolean addFriend(String email);
    void removeFriend(String email, UserType userType);
    void changeNick(String nick);
    void getFriendsList();
}
