package com.example.WhereIsEveryone.mvp;

import androidx.annotation.CallSuper;

public interface Contract {
    interface View {
    }

    interface Presenter<V extends View> {
        V getView();

        void attachView(V view);

        void detachView();
    }

}
