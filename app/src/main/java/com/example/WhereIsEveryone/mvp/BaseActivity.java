package com.example.WhereIsEveryone.mvp;

import android.os.Bundle;
import android.os.PersistableBundle;

import androidx.annotation.CallSuper;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.WhereIsEveryone.DependencyContainer;
import com.example.WhereIsEveryone.MyApplication;

import java.io.InvalidObjectException;

@SuppressWarnings("rawtypes")
public class BaseActivity<P extends Contract.Presenter> extends AppCompatActivity implements Contract.View {

    protected P presenter;

    @SuppressWarnings("unchecked")
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
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
