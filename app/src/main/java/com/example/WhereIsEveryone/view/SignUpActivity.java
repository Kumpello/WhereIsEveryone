package com.example.WhereIsEveryone.view;

import androidx.annotation.StringRes;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Toast;

import com.example.WhereIsEveryone.R;
import com.example.WhereIsEveryone.databinding.ActivitySignUpBinding;
import com.example.WhereIsEveryone.presenter.SignUpPresenter;
import com.example.WhereIsEveryone.view.BaseActivity;
import com.example.WhereIsEveryone.view.LoginActivity;
import com.example.WhereIsEveryone.view.SignUpView;

public class SignUpActivity extends BaseActivity<SignUpPresenter> implements SignUpView {

    private ActivitySignUpBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        presenter = this.getContainer().getSingUpPresenter(this);

        binding.loginText.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();
        });

        binding.btnSignUp.setOnClickListener(v -> {
            // No need to ask for Internet permission
            getSignUpFields();
        });

    }

    private void getSignUpFields() {
        String login = String.valueOf(binding.username.getText());
        String email = String.valueOf(binding.email.getText());
        String password = String.valueOf(binding.password.getText());

        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> {
            presenter.signUpButtonClicked(login, email, password);
        });
    }

    @Override
    public void showError(@StringRes int errorMessage) {
        String error = getString(errorMessage);
        Toast.makeText(getApplicationContext(), error, Toast.LENGTH_SHORT).show();
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