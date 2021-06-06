package com.example.WhereIsEveryone;

import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Toast;

import com.example.WhereIsEveryone.databinding.ActivitySignUpBinding;

public class SignUpActivity extends AppCompatActivity implements LoginView {

    private ActivitySignUpBinding binding;
    private PermissionHandler permissionHandler;
    private SignUpPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        presenter = ((MyApplication) getApplication()).dependencyContainer.getSignUpPresenter(this,
                getString(R.string.login_ip_address),
                getString(R.string.login_failed),
                getString(R.string.connection_error));

        permissionHandler = ((MyApplication) getApplication()).dependencyContainer.getPermissionHandler(this);


        binding.loginText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        binding.btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                permissionHandler.askForPermission(Manifest.permission.INTERNET, getString(R.string.internet_permission_text));
                getSignUpFields();

            }
        });

    }

    private void getSignUpFields(){
        String login = String.valueOf(binding.username.getText());
        String email = String.valueOf(binding.email.getText());
        String password = String.valueOf(binding.password.getText());

        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                presenter.signUpButtonClicked(login, email, password);;
            }});
    }

    @Override
    public void showError(@StringRes int errorMessage) {
        String error = getString(errorMessage);
        Toast.makeText(getApplicationContext(), error, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showError(String error) {
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