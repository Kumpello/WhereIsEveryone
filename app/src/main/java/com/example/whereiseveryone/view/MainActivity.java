package com.example.whereiseveryone.view;

import android.os.Bundle;

import com.example.whereiseveryone.R;
import com.example.whereiseveryone.mvp.BaseActivity;

//What to do with this error?
public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}