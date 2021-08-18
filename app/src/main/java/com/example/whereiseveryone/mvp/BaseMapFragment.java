package com.example.whereiseveryone.mvp;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.example.whereiseveryone.DependencyContainer;
import com.example.whereiseveryone.MyApplication;
import com.google.android.gms.maps.MapFragment;

@SuppressWarnings("rawtypes")
public class BaseMapFragment<P extends Contract.Presenter> extends MapFragment implements Contract.View {

    protected P presenter;

    @SuppressWarnings("unchecked")
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.presenter = (P) getContainer().getPresenter(this);
        presenter.attachView(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        presenter.detachView();
    }

    protected DependencyContainer getContainer() {
        return ((MyApplication) this.getActivity().getApplication()).getContainer();
    }

}
