package com.example.WhereIsEveryone.presenter;

import com.example.WhereIsEveryone.mvp.Contract;
import com.example.WhereIsEveryone.view.LoginView;

public interface LoginPresenter extends Contract.Presenter<LoginView> {
    void loginButtonClicked(String login, String password);
}
