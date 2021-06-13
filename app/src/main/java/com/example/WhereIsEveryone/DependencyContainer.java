package com.example.WhereIsEveryone;

import android.app.Activity;
import android.app.Application;

import androidx.annotation.NonNull;

import com.example.WhereIsEveryone.model.LoginService;
import com.example.WhereIsEveryone.model.LoginServiceImpl;
import com.example.WhereIsEveryone.model.MapService;
import com.example.WhereIsEveryone.model.MapServiceImpl;
import com.example.WhereIsEveryone.model.PermissionHandler;
import com.example.WhereIsEveryone.model.PermissionHandlerImpl;
import com.example.WhereIsEveryone.model.UserService;
import com.example.WhereIsEveryone.model.UserServiceImpl;
import com.example.WhereIsEveryone.mvp.BasePresenter;
import com.example.WhereIsEveryone.mvp.Contract;
import com.example.WhereIsEveryone.presenter.LoginPresenter;
import com.example.WhereIsEveryone.presenter.LoginPresenterImpl;
import com.example.WhereIsEveryone.presenter.MapPresenter;
import com.example.WhereIsEveryone.presenter.MapPresenterImpl;
import com.example.WhereIsEveryone.presenter.SignUpPresenter;
import com.example.WhereIsEveryone.presenter.SignUpPresenterImpl;
import com.example.WhereIsEveryone.view.LoginView;
import com.example.WhereIsEveryone.view.MapView;
import com.example.WhereIsEveryone.view.SignUpView;

import org.jetbrains.annotations.NotNull;


public class DependencyContainer {

    private final Application application;

    private LoginService loginService;
    private UserService userService;


    private String loginIpAddress;

    // TODO: We should have a lock for initializing.
    //       For now let's skip it.

    public DependencyContainer(Application application) {
        this.application = application;
    }

    @NonNull
    public LoginService getLoginService(String ipAddress) {
        if (loginService == null) {
            loginService = new LoginServiceImpl(ipAddress);
        }

        return loginService;
    }

    @NonNull
    public UserService getUserService() {
        if (userService == null) {
            userService = new UserServiceImpl();
        }

        return userService;
    }

    @NonNull
    public String getLoginIpAddress() {
        if (loginIpAddress == null) {
            loginIpAddress = this.application.getString(R.string.login_ip_address);
        }

        return loginIpAddress;
    }

    // We want to create presenters every time,
    // so we shouldn't keep any references to them
    @NonNull
    public LoginPresenter getLoginPresenter() {
        return new LoginPresenterImpl(
                getLoginService(getLoginIpAddress()),
                getUserService()
        );
    }

    @NonNull
    public SignUpPresenter getSingUpPresenter() {
        return new SignUpPresenterImpl(getLoginService(getLoginIpAddress())
        );
    }

    @NotNull
    public MapPresenter getMapPresenter(Activity activity) {
        return new MapPresenterImpl(getMapService(activity), getPermissionHandler(activity));
    }

    @NotNull
    public MapService getMapService(Activity activity) {
        return new MapServiceImpl(activity);
    }

    @NotNull
    public PermissionHandler getPermissionHandler(Activity activity) {
        return new PermissionHandlerImpl(activity);
    }

    @SuppressWarnings("unchecked")
    public <V extends Contract.View> BasePresenter<V> getPresenter(V injector) throws IllegalArgumentException {
        Activity activity;
        if(injector instanceof Activity) {
            activity = (Activity) injector;
        } else {
            throw new IllegalArgumentException("Injector must be Activity object");
        }

        if (injector instanceof SignUpView) {
            // ugly, but it'll work
            return (BasePresenter<V>) getSingUpPresenter();
        } else if (injector instanceof LoginView) {
            return (BasePresenter<V>) getLoginPresenter();
        } else if (injector instanceof MapView) {
            return (BasePresenter<V>) getMapPresenter(activity);
        }

        throw new IllegalArgumentException("no presenter for such a view");
    }
}
