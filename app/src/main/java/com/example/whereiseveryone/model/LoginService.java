package com.example.whereiseveryone.model;

import com.example.whereiseveryone.utils.Consumer;

public interface LoginService {
    void login(Consumer<LoginResult> consumer);

    void signUp(Consumer<LoginResult> consumer);

    void getEmailAndPassword(String email, String password);

}
