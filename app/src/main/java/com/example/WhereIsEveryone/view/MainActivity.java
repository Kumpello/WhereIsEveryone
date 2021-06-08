package com.example.WhereIsEveryone.view;

import android.os.Bundle;

import com.example.WhereIsEveryone.R;
import com.example.WhereIsEveryone.mvp.BaseActivity;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}