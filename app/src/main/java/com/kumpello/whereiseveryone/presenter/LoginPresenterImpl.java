package com.kumpello.whereiseveryone.presenter;


import static com.kumpello.whereiseveryone.utils.TextUtils.isNullOrEmpty;

import android.content.Intent;

import com.kumpello.whereiseveryone.R;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.kumpello.whereiseveryone.model.LoginService;
import com.kumpello.whereiseveryone.model.UserService;
import com.kumpello.whereiseveryone.model.UserType;
import com.kumpello.whereiseveryone.mvp.BasePresenter;
import com.kumpello.whereiseveryone.view.LoginView;

public class LoginPresenterImpl extends BasePresenter<LoginView> implements LoginPresenter {

    private final LoginService loginService;
    private final UserService userService;

    public LoginPresenterImpl(LoginService loginService, UserService userService) {
        this.loginService = loginService;
        this.userService = userService;
        if (loginService.checkIfLoggedByGoogle()) {
            view.showSuccess();
        }
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
            userService.saveLoginType(UserType.EMAIL);

            view.showSuccess();
        });

    }

    @Override
    public void googleButtonClicked() {
        GoogleSignInClient googleSignInClient = loginService.getGoogleSignInClient();
        Intent signInIntent = googleSignInClient.getSignInIntent();
        //ToDo Some kind of checkup if login was correct/proceeded
        view.loginByGoogle(signInIntent);
        userService.saveToken(loginService.getGoogleAccount().getIdToken());
        userService.saveEmail(loginService.getGoogleAccount().getEmail());
        userService.saveLoginType(UserType.GOOGLE);
    }
}
