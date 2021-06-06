package com.example.WhereIsEveryone.view;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.StringRes;

import com.example.WhereIsEveryone.presenter.LoginPresenter;
import com.example.WhereIsEveryone.R;
import com.example.WhereIsEveryone.databinding.ActivityLoginBinding;
import com.example.WhereIsEveryone.todo.MapActivity;

// Let's extend BaseActivity that keeps some helpers method
// like "getContainer"
public class LoginActivity extends BaseActivity<LoginPresenter> implements LoginView {

    private ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        presenter = getContainer().getLoginPresenter(this);

        // TODO(kumpel): Look how lambda can simplify the code ;)
        //               This is exactly the same like "new View.OnClick..."
        binding.signUpText.setOnClickListener(v -> {
            // FYI: We can keep it like here, but it would be nice to do at some time
            // NavigatorClass that gets this responsibility of ouf here
            // And move it to the Model class.
            Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
            startActivity(intent);
            finish();
        });

        binding.btnLogin.setOnClickListener(v -> {
            // TODO(kumpel): We don't ask permission handler directly!
            //               We tell the presenter to do that!
            //               Then the presenter tells the view, for example, to show the dialog.
            //
            // I left the code to show the idea, but FYI
            // Internet permission is not runtime, so we don't need to ask for permission.
            // So the code may be removed.
            //
            // presenter.permissionNeeded(Manifest.permission.INTERNET);
            getLoginFields();
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