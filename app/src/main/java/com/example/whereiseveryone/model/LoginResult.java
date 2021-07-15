package com.example.whereiseveryone.model;

import androidx.annotation.Nullable;

public class LoginResult {


    // TODO(kumpel): We should split this class into Login/SignUp
    public enum Error {
        SignUpFailed,
        EmailAlreadyInUse,
        PasswordTooShort,
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
