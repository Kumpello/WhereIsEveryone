package com.example.WhereIsEveryone.presenter;

import androidx.annotation.Nullable;

import com.example.WhereIsEveryone.model.LoginResult;
import com.example.WhereIsEveryone.model.LoginService;
import com.example.WhereIsEveryone.R;
import com.example.WhereIsEveryone.model.UserService;
import com.example.WhereIsEveryone.view.LoginView;

public class LoginPresenterImpl implements LoginPresenter {

    private final LoginView view;
    private final LoginService loginService;
    private final UserService userService;

    public LoginPresenterImpl(LoginView view, LoginService loginService, UserService userService) {
        this.view = view;
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

        LoginResult loginResult = loginService.login(login, password);

        if (loginResult.getError() != null) {
            // TODO(kumpel): Basing on error type we will get proper string
            switch (loginResult.getError()) {
                case LoginFailed:
                    view.showError(R.string.login_failed);
                case ConnectionError:
                    view.showError(R.string.connection_error);
            }

            return;
        }

        userService.saveToken(loginResult.getToken());

        view.showSuccess();
    }

    private boolean isNullOrEmpty(@Nullable String string) {
        return string == null || string.isEmpty();
    }
}
