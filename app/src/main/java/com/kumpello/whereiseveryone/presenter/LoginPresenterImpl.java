package com.kumpello.whereiseveryone.presenter;


import static com.kumpello.whereiseveryone.utils.TextUtils.isNullOrEmpty;

import com.example.whereiseveryone.R;
import com.kumpello.whereiseveryone.model.LoginService;
import com.kumpello.whereiseveryone.model.UserService;
import com.kumpello.whereiseveryone.mvp.BasePresenter;
import com.kumpello.whereiseveryone.view.LoginView;

public class LoginPresenterImpl extends BasePresenter<LoginView> implements LoginPresenter {

    private final LoginService loginService;
    private final UserService userService;

    public LoginPresenterImpl(LoginService loginService, UserService userService) {
        this.loginService = loginService;
        this.userService = userService;
    }

    @Override
    public void loginButtonClicked(String login, String password) {
        if (isNullOrEmpty(login) || isNullOrEmpty(password)) {
            view.showError(R.string.empty_fields);
            return;
        }

        view.showProgress();

        loginService.getEmailAndPassword(login, password);

        loginService.login(value -> {
            if (value.getError() != null) {
                //Todo: switch output by error type
                view.showError(R.string.signup_failed);
                return;
            }
            userService.saveToken(value.getToken());
            userService.saveEmail(login);

            view.showSuccess();
        });

    }
}
