package com.kumpello.whereiseveryone.view;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.StringRes;

import com.example.whereiseveryone.databinding.ActivityLoginBinding;
import com.kumpello.whereiseveryone.mvp.BaseActivity;
import com.kumpello.whereiseveryone.presenter.LoginPresenter;

import static android.view.View.GONE;


public class LoginActivity extends BaseActivity<LoginPresenter> implements LoginView {

    private ActivityLoginBinding binding;

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
}