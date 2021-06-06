package com.example.WhereIsEveryone;

import android.app.Application;

public class MyApplication extends Application {
    private UserService userService;

    public UserService getUserService() {
        return userService;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public DependencyContainer dependencyContainer = new DependencyContainer();
}
