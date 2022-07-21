package com.kumpello.whereiseveryone.presenter;

import com.kumpello.whereiseveryone.mvp.Contract;
import com.kumpello.whereiseveryone.view.SignUpView;

public interface SignUpPresenter extends Contract.Presenter<SignUpView> {
    void signUpButtonClicked(String password, String email);
    boolean checkIfLogged();
}
