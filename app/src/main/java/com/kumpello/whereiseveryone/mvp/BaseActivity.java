package com.kumpello.whereiseveryone.mvp;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.kumpello.whereiseveryone.DependencyContainer;
import com.kumpello.whereiseveryone.MyApplication;

@SuppressWarnings("rawtypes")
public class BaseActivity<P extends com.kumpello.whereiseveryone.mvp.Contract.Presenter> extends AppCompatActivity implements com.kumpello.whereiseveryone.mvp.Contract.View {

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
