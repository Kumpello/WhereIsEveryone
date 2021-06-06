package com.example.WhereIsEveryone.view;

import androidx.appcompat.app.AppCompatActivity;

import com.example.WhereIsEveryone.DependencyContainer;
import com.example.WhereIsEveryone.MyApplication;

/**
 * BaseActivity is a base for all other activities in the project.
 */
public class BaseActivity extends AppCompatActivity {

    protected DependencyContainer getContainer() {
        return ((MyApplication) this.getApplication()).getContainer();
    }

}
