package com.kumpello.whereiseveryone.presenter;

import com.kumpello.whereiseveryone.model.UserType;
import com.kumpello.whereiseveryone.mvp.Contract;
import com.kumpello.whereiseveryone.view.LoginView;

public interface LoginPresenter extends Contract.Presenter<LoginView> {
    void loginButtonClicked(String login, String password);
    void googleButtonClicked();
    void saveUserData(UserType userType);
}
