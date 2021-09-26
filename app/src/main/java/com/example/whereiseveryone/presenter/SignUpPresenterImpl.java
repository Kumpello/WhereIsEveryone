package com.example.whereiseveryone.presenter;

import com.example.whereiseveryone.R;
import com.example.whereiseveryone.model.LoginService;
import com.example.whereiseveryone.mvp.BasePresenter;
import com.example.whereiseveryone.view.SignUpView;

import static com.example.whereiseveryone.utils.TextUtils.isNullOrEmpty;

public class SignUpPresenterImpl extends BasePresenter<SignUpView> implements SignUpPresenter {

    private final LoginService loginService;

    public SignUpPresenterImpl(LoginService loginService) {
        this.loginService = loginService;
    }

    @Override
    public void signUpButtonClicked(String email, String password) {
        if (isNullOrEmpty(password) || isNullOrEmpty(email)) {
            return;
        }

        view.showProgress();

        loginService.getEmailAndPassword(email, password);

        loginService.signUp(value -> {
            if (value.getError() != null) {
                view.showError(R.string.signup_failed);
                return;
            }

            view.showSuccess();
        });

    }

    @Override
    public boolean checkIfLogged() {
        return loginService.checkIfLogged();
    }
}
