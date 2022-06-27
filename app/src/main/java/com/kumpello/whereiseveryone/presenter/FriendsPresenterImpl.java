package com.kumpello.whereiseveryone.presenter;

import android.util.Log;

import com.kumpello.whereiseveryone.model.FriendsService;
import com.kumpello.whereiseveryone.model.User;
import com.kumpello.whereiseveryone.model.UserType;
import com.kumpello.whereiseveryone.mvp.BasePresenter;
import com.kumpello.whereiseveryone.utils.CallbackIterator;
import com.kumpello.whereiseveryone.utils.OnResult;
import com.kumpello.whereiseveryone.view.FriendsView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FriendsPresenterImpl extends BasePresenter<FriendsView> implements FriendsPresenter{

    FriendsService friendsService;

    public FriendsPresenterImpl(FriendsService friendsService) {
        this.friendsService = friendsService;
    }

    @Override
    public boolean addFriend(String email) {
        email = email.toLowerCase();
        List<Boolean> responses = new ArrayList<>();
        for (UserType userType : UserType.values()) {
            responses.add(friendsService.addFriend(email, userType, new OnResult<String>() {
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
            }));
        }
        return responses.contains(Boolean.TRUE);
    }

    @Override
    public void removeFriend(String email, UserType userType) {
        friendsService.removeFriend(email, userType);
        view.removeFriend(email);
    }

    @Override
    public void changeNick(String nick) {
        Log.d("FriendsPresenter", "Nick set to " + nick);
        friendsService.changeNick(nick);
    }

    @Override
    public void getFriendsList() {
        //Todo make local list and check if it is up to date
        friendsService.getSize(new OnResult<Integer>() {
            @Override
            public void onSuccess(Integer result) {
                List<User> allUsers = new ArrayList<>(result);
                allUsers = Collections.synchronizedList(allUsers);

                friendsService.getFriendsList(new CallbackIterator<User>() {
                    @Override
                    public void onNext(User result) {
                        view.addFriendToAdapter(result);
                        Log.d("Presenter - getFriendsList - onSuccess", result.toString());
                    }

                    @Override
                    public void onError(Throwable error) {
                        //TODO
                    }
                });
            }

            @Override
            public void onError(Throwable error) {
                //Todo
            }
        });
    }
}
