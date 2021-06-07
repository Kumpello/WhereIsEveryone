package com.example.WhereIsEveryone.mvp;

public interface Contract {
    interface View {
    }

    interface Presenter<V extends View> {
        V getView();

        // This is quite important - when the view is created/destroy it
        // should be attached/detached (a guarantee there will be no context leaks)
        void attachView(V view);

        void detachView();
    }

}
