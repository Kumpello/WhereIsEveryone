package com.example.whereiseveryone;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.vishnusivadas.advanced_httpurlconnection.PutData;

public class SignUp extends AppCompatActivity {

    private TextInputEditText textInputEditTextusername, textInputEditTextpassword, textInputEditTextemail;
    private Button signUp;
    private TextView textViewLogin;
    private ProgressBar progressBar;
    private Resources resources;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        textInputEditTextusername = findViewById(R.id.username);
        textInputEditTextemail = findViewById(R.id.email);
        textInputEditTextpassword = findViewById(R.id.password);
        signUp = findViewById(R.id.btnSignUp);
        textViewLogin = findViewById(R.id.loginText);
        progressBar = findViewById(R.id.progressBar);
        resources = getResources();



        textViewLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
                finish();
            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                askForPermissions(Manifest.permission.INTERNET, resources.getString(R.string.internet_permission_text));

                String username, password, email;
                username = String.valueOf(textInputEditTextusername.getText());
                password = String.valueOf(textInputEditTextpassword.getText());
                email    = String.valueOf(textInputEditTextemail.getText());
                if (!username.equals("") && !password.equals("") && !username.equals("")){
                    progressBar.setVisibility(View.VISIBLE);
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            String[] field = new String[3];
                            field[0] = "username";
                            field[1] = "password";
                            field[2] = "email";
                            String[] data = new String[3];
                            data[0] = username;
                            data[1] = password;
                            data[2] = email;
                            PutData putData = new PutData("http://192.168.8.101/WhereIsEveryone/signup.php", "POST", field, data);
                            if (putData.startPut()) {
                                if (putData.onComplete()) {
                                    progressBar.setVisibility(View.GONE);
                                    String result = putData.getResult();
                                    if (result.equals("Sign Up Success")) {
                                        Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(getApplicationContext(), Login.class);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        }
                    });
                } else {
                    Toast.makeText(getApplicationContext(), "All fields required", Toast.LENGTH_SHORT).show();
                }
                
            }
        });

    }

    public void askForPermissions(String permission, String message) {
        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
            //
        } else if (shouldShowRequestPermissionRationale(permission)) {
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
            builder.setTitle(resources.getString(R.string.permission_needed));
            builder.setMessage(message);
            builder.setNegativeButton(resources.getString(R.string.decline), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }});
            builder.setPositiveButton(resources.getString(R.string.accept), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    requestPermissions(new String[]{permission}, 2137);
                    dialog.dismiss();
                }});
            builder.show();
        } else {
            requestPermissions(new String[] {permission}, 2137);
        }
    }
}