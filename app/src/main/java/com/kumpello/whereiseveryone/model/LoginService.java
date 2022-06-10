package com.kumpello.whereiseveryone.model;

import com.kumpello.whereiseveryone.utils.Consumer;

public interface LoginService {
    void login(Consumer<LoginResult> consumer);

    void signUp(Consumer<LoginResult> consumer);

    void getEmailAndPassword(String email, String password);

    boolean checkIfLogged();

}
