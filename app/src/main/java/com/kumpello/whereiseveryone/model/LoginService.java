package com.kumpello.whereiseveryone.model;

import android.content.Intent;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.tasks.Task;
import com.kumpello.whereiseveryone.utils.Consumer;

public interface LoginService {
    void login(Consumer<LoginResult> consumer);

    void signUp(Consumer<LoginResult> consumer);

    void getEmailAndPassword(String email, String password);

    boolean checkIfLogged();

    boolean checkIfLoggedByGoogle();

    GoogleSignInClient getGoogleSignInClient();

    Task<GoogleSignInAccount> getGoogleAccount(Intent intent);

}
