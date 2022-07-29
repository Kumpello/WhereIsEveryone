package com.kumpello.whereiseveryone.presenter;


import static com.kumpello.whereiseveryone.utils.TextUtils.isNullOrEmpty;

import android.content.Intent;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.common.ConnectionResult;
import com.google.firebase.auth.GoogleAuthProvider;
import com.kumpello.whereiseveryone.R;
import com.kumpello.whereiseveryone.model.LoginResult;
import com.kumpello.whereiseveryone.model.LoginService;
import com.kumpello.whereiseveryone.model.UserService;
import com.kumpello.whereiseveryone.model.UserType;
import com.kumpello.whereiseveryone.mvp.BasePresenter;
import com.kumpello.whereiseveryone.utils.Consumer;
import com.kumpello.whereiseveryone.utils.OnResult;
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
    }

    @Override
    public void saveUserData(UserType userType, Intent intent, OnResult<ConnectionResult> connectionResult) {
        switch (userType) {
            case GOOGLE:
                GoogleSignInAccount account = loginService.getGoogleAccount(intent).getResult();
                if (account != null) {
                    loginService.loginByGoogle(GoogleAuthProvider.getCredential(account.getIdToken(), null), new Consumer<LoginResult>() {
                        @Override
                        public void accept(LoginResult value) {
                            userService.firstRun(true);
                            userService.saveToken(value.getToken());
                            userService.saveEmail(account.getEmail());
                            userService.saveLoginType(UserType.GOOGLE);
                            connectionResult.onSuccess(ConnectionResult.RESULT_SUCCESS);
                        }
                    });
                } else {
                    connectionResult.onError(new Exception("Getting account problem"));
                }
                break;
            //More to be added!
        }
    }
}
