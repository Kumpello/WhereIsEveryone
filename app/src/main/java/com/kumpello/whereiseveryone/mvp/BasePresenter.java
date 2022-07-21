package com.kumpello.whereiseveryone.mvp;

import androidx.annotation.CallSuper;

public class BasePresenter<V extends com.kumpello.whereiseveryone.mvp.Contract.View> implements com.kumpello.whereiseveryone.mvp.Contract.Presenter<V> {

    protected V view;

    @Override
    public V getView() {
        return view;
    }

    @Override
    @CallSuper
    public void attachView(final V view) {
        this.view = view;
    }

    @Override
    @CallSuper
    public void detachView() {
        this.view = null;
    }
}
