package com.example.WhereIsEveryone;

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
            view.showError(loginResult.getError());
            return;
        }

        userService.saveToken(loginResult.getToken());



        view.showSuccess();
    }

    @Override
    public UserService getUserService() {
        return userService;
    }

    private boolean isNullOrEmpty(String string) {
        return string.equals(null) || string.isEmpty();
    }
}
