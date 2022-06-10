package com.kumpello.whereiseveryone.model;

import android.app.Activity;
import android.content.res.Resources;
import android.util.Log;

import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.kumpello.whereiseveryone.utils.Consumer;


public class LoginServiceImpl implements LoginService {


    private static final String TAG = "EmailPassword";
    private final FirebaseAuth mAuth;
    private String[] emailAndPassword;
    private SignInClient oneTapClient;
    private BeginSignInRequest signInRequest;

    public LoginServiceImpl(Resources resources, Activity activity) {
        mAuth = FirebaseAuth.getInstance();
/*
        oneTapClient = Identity.getSignInClient(activity);
        signInRequest = BeginSignInRequest.builder()
                .setPasswordRequestOptions(BeginSignInRequest.PasswordRequestOptions.builder()
                        .setSupported(true)
                        .build())
                .setGoogleIdTokenRequestOptions(BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                        .setSupported(true)
                        // Your server's client ID, not your Android client ID.
                        .setServerClientId(getString(R.string.default_web_client_id))
                        // Only show accounts previously used to sign in.
                        .setFilterByAuthorizedAccounts(true)
                        .build())
                // Automatically sign in when exactly one credential is retrieved.
                .setAutoSelectEnabled(true)
                .build();
*/

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
