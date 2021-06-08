package com.example.WhereIsEveryone;

import android.app.Application;

public class MyApplication extends Application {

    private final DependencyContainer container = new DependencyContainer(this);

    public DependencyContainer getContainer() {
        return container;
    }
}
