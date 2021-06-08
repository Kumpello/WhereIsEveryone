package com.example.WhereIsEveryone.model;

public interface LoginService {
    LoginResult login(String login, String password);

    LoginResult signUp(String login, String password, String email);
}
