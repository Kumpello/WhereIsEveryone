package com.kumpello.whereiseveryone.view;

import static android.view.View.GONE;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.StringRes;

import com.google.android.gms.common.ConnectionResult;
import com.kumpello.whereiseveryone.databinding.ActivityLoginBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.kumpello.whereiseveryone.model.UserType;
import com.kumpello.whereiseveryone.mvp.BaseActivity;
import com.kumpello.whereiseveryone.presenter.LoginPresenter;
import com.kumpello.whereiseveryone.utils.OnResult;

import java.io.FileNotFoundException;


public class LoginActivity extends BaseActivity<LoginPresenter> implements LoginView {

    private ActivityLoginBinding binding;
    private static final int GOOGLE_CODE = 420;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        binding.signUpText.setOnClickListener(v -> {
            // FYI: We can keep it like here, but it would be nice to do at some time
            // NavigatorClass that gets this responsibility of ouf here
            // And move it to the Model class.
            Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
            startActivity(intent);
            finish();
        });

        binding.btnLogin.setOnClickListener(v -> getLoginFields());

        binding.btnGoogleSignIn.setOnClickListener(v -> googleButtonClicked());
    }

    @Override
    public void showError(@StringRes int errorMessage) {
        String error = getString(errorMessage);
        Toast.makeText(getApplicationContext(), error, Toast.LENGTH_SHORT).show();
        binding.progressBar.setVisibility(GONE);
    }

    @Override
    public void showProgress() {
        binding.progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void showSuccess() {
        binding.progressBar.setVisibility(GONE);
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }


    private void getLoginFields() {
        String username = String.valueOf(binding.username.getText());
        String password = String.valueOf(binding.password.getText());

        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> presenter.loginButtonClicked(username, password));
    }

    private void googleButtonClicked() {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> presenter.googleButtonClicked());
    }

    public void loginByGoogle(Intent intent) {
        startActivityForResult(intent, GOOGLE_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GOOGLE_CODE) {
            presenter.saveUserData(UserType.GOOGLE, data, new OnResult<ConnectionResult>() {
                @Override
                public void onSuccess(ConnectionResult result) {
                    showSuccess();
                }

                @Override
                public void onError(Throwable error) {
                    Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                    error.printStackTrace();
                }
            });

        }

    }
}