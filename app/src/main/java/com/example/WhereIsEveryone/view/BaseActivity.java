package com.example.WhereIsEveryone.view;

import androidx.appcompat.app.AppCompatActivity;

import com.example.WhereIsEveryone.DependencyContainer;
import com.example.WhereIsEveryone.MyApplication;
import com.example.WhereIsEveryone.mvp.Contract;

/**
 * BaseActivity is a base for all other activities in the project.
 */
public class BaseActivity<P extends Contract.Presenter> extends AppCompatActivity {

    protected P presenter;

    protected DependencyContainer getContainer() {
        return ((MyApplication) this.getApplication()).getContainer();
    }

}
