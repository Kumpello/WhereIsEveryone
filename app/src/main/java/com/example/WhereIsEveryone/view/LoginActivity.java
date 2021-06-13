package com.example.WhereIsEveryone.view;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.StringRes;

import com.example.WhereIsEveryone.mvp.BaseActivity;
import com.example.WhereIsEveryone.presenter.LoginPresenter;
import com.example.WhereIsEveryone.R;
import com.example.WhereIsEveryone.databinding.ActivityLoginBinding;


public class LoginActivity extends BaseActivity<LoginPresenter> implements LoginView {

    private ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

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
    }


    @Override
    public void showProgress() {
        binding.progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void showSuccess() {
        binding.progressBar.setVisibility(View.GONE);
        Intent intent = new Intent(getApplicationContext(), MapActivity.class);
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