package com.example.WhereIsEveryone.mvp;

import androidx.annotation.CallSuper;
import androidx.appcompat.app.AppCompatActivity;

public class BaseActivity<P extends BasePresenter> extends AppCompatActivity implements Contract.View {

    protected P presenter;

    @Override
    @CallSuper
    protected void onStart() {
        super.onStart();
        presenter.attachView(this);
    }

    @Override
    @CallSuper
    protected void onStop() {
        super.onStop();
        presenter.detachView();
    }
}
