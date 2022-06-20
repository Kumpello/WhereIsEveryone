package com.kumpello.whereiseveryone.model;

import android.app.Activity;
import android.content.res.Resources;
import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.kumpello.whereiseveryone.utils.Consumer;


public class LoginServiceImpl implements LoginService {


    private static final String TAG = "EmailPassword";
    private final FirebaseAuth mAuth;
    private String[] emailAndPassword;
    private final Activity activity;
    private GoogleSignInOptions googleSignInOptions;
    private GoogleSignInClient googleSignInClient;
    private boolean userSignedIn;
    private GoogleSignInAccount account;

    public LoginServiceImpl(Resources resources, Activity activity) {
        this.activity = activity;
        mAuth = FirebaseAuth.getInstance();
        userSignedIn = false;
        googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        googleSignInClient = GoogleSignIn.getClient(activity, googleSignInOptions);

        account = GoogleSignIn.getLastSignedInAccount(activity);
        if (account != null) {
            userSignedIn = true;
        }


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

    @Override
    public boolean checkIfLoggedByGoogle(){
        return userSignedIn;
    }

    @Override
    public GoogleSignInClient getGoogleSignInClient() {
        return googleSignInClient;
    }

    @Override
    public GoogleSignInAccount getGoogleAccount() {
        return GoogleSignIn.getLastSignedInAccount(activity);
    }
}
