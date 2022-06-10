package com.kumpello.whereiseveryone.view;

import androidx.annotation.StringRes;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Toast;

import com.example.whereiseveryone.R;
import com.example.whereiseveryone.databinding.ActivitySignUpBinding;
import com.kumpello.whereiseveryone.mvp.BaseActivity;
import com.kumpello.whereiseveryone.presenter.SignUpPresenter;

public class SignUpActivity extends BaseActivity<SignUpPresenter> implements SignUpView {

    private ActivitySignUpBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        if (presenter.checkIfLogged()) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }

        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        binding.loginText.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();
        });

        binding.btnSignUp.setOnClickListener(v -> getSignUpFields());

    }

    private void getSignUpFields() {
        //What to do?
        String email = binding.email.getText().toString();
        String password = binding.password.getText().toString();

        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> presenter.signUpButtonClicked(email, password));
    }

    @Override
    public void showError(@StringRes int errorMessage) {
        String error = getString(errorMessage);
        Toast.makeText(getApplicationContext(), error, Toast.LENGTH_SHORT).show();
        binding.progressBar.setVisibility(View.GONE);
    }

    @Override
    public void showProgress() {
        binding.progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void showSuccess() {
        binding.progressBar.setVisibility(View.GONE);
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
        finish();
    }
}