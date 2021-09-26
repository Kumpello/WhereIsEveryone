package com.example.whereiseveryone.model;

import android.util.Log;


import com.example.whereiseveryone.utils.Consumer;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class LoginServiceImpl implements LoginService {


    private static final String TAG = "EmailPassword";
    private final FirebaseAuth mAuth;
    private String[] emailAndPassword;

    public LoginServiceImpl() {
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void login(Consumer<LoginResult> consumer) {
        String email = emailAndPassword[0];
        String password = emailAndPassword[1];

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithEmail:success");
                        final FirebaseUser user = mAuth.getCurrentUser();
                        assert user != null;
                        consumer.accept(new LoginResult(null, user.getUid()));
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithEmail:failure", task.getException());
                        consumer.accept(new LoginResult(LoginResult.Error.LoginFailed, null));
                    }
                });
    }

    @Override
    public void signUp(Consumer<LoginResult> consumer) {
        String email = emailAndPassword[0];
        String password = emailAndPassword[1];

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "createUserWithEmail:success");
                        final FirebaseUser user = mAuth.getCurrentUser();
                        assert user != null;
                        consumer.accept(new LoginResult(null, user.getUid()));
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                        //Todo: Error verification
                        consumer.accept(new LoginResult(LoginResult.Error.SignUpFailed, null));
                    }
                });
    }

    @Override
    public void getEmailAndPassword(String email, String password) {
        emailAndPassword = new String[] {email, password};
    }

    @Override
    public boolean checkIfLogged() {
        return mAuth.getCurrentUser() != null;
    }
}
