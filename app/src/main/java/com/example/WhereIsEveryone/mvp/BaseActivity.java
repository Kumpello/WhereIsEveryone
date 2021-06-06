package com.example.WhereIsEveryone.mvp;

import androidx.annotation.CallSuper;
import androidx.appcompat.app.AppCompatActivity;

import com.example.WhereIsEveryone.DependencyContainer;
import com.example.WhereIsEveryone.MyApplication;

public class BaseActivity<P extends Contract.Presenter> extends AppCompatActivity implements Contract.View {

    protected P presenter;

    @Override
    @CallSuper
    protected void onStart() {
        super.onStart();
        this.presenter = (P) getContainer().getPresenter(this);
        presenter.attachView(this);
    }

    @Override
    @CallSuper
    protected void onStop() {
        super.onStop();
        presenter.detachView();
    }

    protected DependencyContainer getContainer() {
        return ((MyApplication) this.getApplication()).getContainer();
    }

}
