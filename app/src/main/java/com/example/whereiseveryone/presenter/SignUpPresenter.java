package com.example.whereiseveryone.presenter;

import com.example.whereiseveryone.mvp.Contract;
import com.example.whereiseveryone.view.SignUpView;

public interface SignUpPresenter extends Contract.Presenter<SignUpView> {
    void signUpButtonClicked(String password, String email);
    boolean checkIfLogged();
}
