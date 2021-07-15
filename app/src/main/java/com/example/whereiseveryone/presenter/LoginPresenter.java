package com.example.whereiseveryone.presenter;

import com.example.whereiseveryone.mvp.Contract;
import com.example.whereiseveryone.view.LoginView;

public interface LoginPresenter extends Contract.Presenter<LoginView> {
    void loginButtonClicked(String login, String password);
}
