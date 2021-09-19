package com.example.whereiseveryone.presenter;

import android.util.Log;

import com.example.whereiseveryone.model.FriendsService;
import com.example.whereiseveryone.model.User;
import com.example.whereiseveryone.mvp.BasePresenter;
import com.example.whereiseveryone.utils.OnResult;
import com.example.whereiseveryone.view.FriendsView;

import java.util.List;

public class FriendsPresenterImpl extends BasePresenter<FriendsView> implements FriendsPresenter{

    FriendsService friendsService;

    public FriendsPresenterImpl(FriendsService friendsService) {
        this.friendsService = friendsService;
    }

    @Override
    public boolean addFriend(String email) {
        email = email.toLowerCase();
        return friendsService.addFriend(email.trim(), new OnResult<String>() {
            @Override
            public void onSuccess(String result) {
                friendsService.getUserIDbyEmail(result, new OnResult<String>() {
                    @Override
                    public void onSuccess(String result) {
                        friendsService.getUser(result, new OnResult<User>() {
                            @Override
                            public void onSuccess(User result) {
                                view.addFriendToAdapter(result);
                            }

                            @Override
                            public void onError(Throwable error) {
                                //TODO
                            }
                        });
                    }

                    @Override
                    public void onError(Throwable error) {
                        //TODO
                    }
                });
            }

            @Override
            public void onError(Throwable error) {
                // notify the view - error
            }
        });
    }

    @Override
    public void removeFriend(String email) {
        friendsService.removeFriend(email);
        view.removeFriend(email);
    }

    @Override
    public void changeNick(String nick) {
        Log.d("FriendsPresenter", "Nick set to " + nick);
        friendsService.changeNick(nick);
    }

    @Override
    public void getFriendsList() {
        friendsService.getFriendsList(new OnResult<List<User>>() {
            @Override
            public void onSuccess(List<User> result) {
                Log.d("Presenter - getFriendsList - onSuccess", result.toString());
                for (User user : result ) {
                    view.addFriendToAdapter(user);
                }
            }

            @Override
            public void onError(Throwable error) {
                //TODO
            }
        });
    }
}
