package com.example.WhereIsEveryone.model;

import androidx.annotation.Nullable;

public class LoginResult {

    // We don't want to keep any strings to display here.
    // It's up to presenter to tell the view what should be shown.
    // In this case we can return an Enum, and it's up to the presenter
    // to map enum-error to proper string.

    // TODO(kumpel): We should split this class into Login/SignUp
    public enum Error {
        SignUpFailed,
        LoginFailed,
        ConnectionError
    }

    private final Error error;
    private final String token;

    public LoginResult(Error error, String token) {
        this.error = error;
        this.token = token;
    }

    @Nullable
    public Error getError() {
        return error;
    }

    @Nullable
    public String getToken() {
        return token;
    }
}
