package com.example.WhereIsEveryone.presenter;

import com.example.WhereIsEveryone.mvp.Contract;
import com.example.WhereIsEveryone.view.SignUpView;

public interface SignUpPresenter extends Contract.Presenter<SignUpView> {
    void signUpButtonClicked(String login, String password, String email);
}
