package com.example.WhereIsEveryone.presenter;

import com.example.WhereIsEveryone.R;
import com.example.WhereIsEveryone.model.LoginResult;
import com.example.WhereIsEveryone.model.LoginService;
import com.example.WhereIsEveryone.mvp.BasePresenter;
import com.example.WhereIsEveryone.view.SignUpView;

public class SignUpPresenterImpl extends BasePresenter<SignUpView> implements SignUpPresenter {

    private final LoginService loginService;

    public SignUpPresenterImpl(SignUpView view, LoginService loginService) {
        this.view = view;
        this.loginService = loginService;
    }

    @Override
    public void signUpButtonClicked(String login, String password, String email) {
        if (isNullOrEmpty(login) || isNullOrEmpty(password) || isNullOrEmpty(email)) {
            return;
        }

        view.showProgress();

        LoginResult signUpResult = loginService.signUp(login, password, email);

        if (signUpResult.getError() != null) {
            view.showError(R.string.signup_failed);
            return;
        }

        view.showSuccess();
    }

    private boolean isNullOrEmpty(String string) {
        return string == null || string.isEmpty();
    }
}
