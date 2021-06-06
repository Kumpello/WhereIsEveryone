package com.example.WhereIsEveryone;

public class LoginResult {
    private final String ERROR;
    private final String TOKEN;

    public LoginResult(String error, String token) {
        this.ERROR = error;
        this.TOKEN = token;
    }

    public String getError() {
        return ERROR;
    }

    public String getToken() {
        return TOKEN;
    }
}
