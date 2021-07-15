package com.example.whereiseveryone.mvp;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.whereiseveryone.DependencyContainer;
import com.example.whereiseveryone.MyApplication;

@SuppressWarnings("rawtypes")
public class BaseActivity<P extends Contract.Presenter> extends AppCompatActivity implements Contract.View {

    protected P presenter;

    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.presenter = (P) getContainer().getPresenter(this);
        presenter.attachView(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.detachView();
    }

    protected DependencyContainer getContainer() {
        return ((MyApplication) this.getApplication()).getContainer();
    }

}
