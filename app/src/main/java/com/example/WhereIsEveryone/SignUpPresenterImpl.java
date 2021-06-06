package com.example.WhereIsEveryone;

public class SignUpPresenterImpl implements SignUpPresenter {

    private final LoginView view;
    private final SignUpService signUpService;

    public SignUpPresenterImpl(LoginView view, SignUpService signUpService) {
        this.view = view;
        this.signUpService = signUpService;
    }

    @Override
    public void signUpButtonClicked(String login, String password, String email) {
        if (isNullOrEmpty(login) || isNullOrEmpty(password) || isNullOrEmpty(email)) {
            return;
        }

        view.showProgress();

        LoginResult signUpResult = signUpService.signUp(login, password, email);

        if (signUpResult.getError() != null) {
            view.showError(signUpResult.getError());
            return;
        }

        view.showSuccess();
    }

    private boolean isNullOrEmpty(String string) {
        return string.equals(null) || string.isEmpty();
    }
}
