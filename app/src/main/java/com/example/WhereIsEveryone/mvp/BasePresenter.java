package com.example.WhereIsEveryone.mvp;

public class BasePresenter<V extends Contract.View> implements Contract.Presenter<V> {

    protected V view;

    @Override
    public V getView() {
        return view;
    }

    @Override
    public void attachView(final V view) {
        this.view = view;
    }

    @Override
    public void detachView() {
        this.view = null;
    }
}
