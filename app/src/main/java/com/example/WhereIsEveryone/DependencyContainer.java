package com.example.WhereIsEveryone;

import android.app.Application;

import androidx.annotation.NonNull;

import com.example.WhereIsEveryone.model.LoginService;
import com.example.WhereIsEveryone.model.LoginServiceImpl;
import com.example.WhereIsEveryone.model.UserService;
import com.example.WhereIsEveryone.model.UserServiceImpl;
import com.example.WhereIsEveryone.mvp.BasePresenter;
import com.example.WhereIsEveryone.mvp.Contract;
import com.example.WhereIsEveryone.presenter.LoginPresenter;
import com.example.WhereIsEveryone.presenter.LoginPresenterImpl;
import com.example.WhereIsEveryone.presenter.SignUpPresenter;
import com.example.WhereIsEveryone.presenter.SignUpPresenterImpl;
import com.example.WhereIsEveryone.view.LoginView;
import com.example.WhereIsEveryone.view.SignUpView;

public class DependencyContainer {

    private final Application application;

    // Model bindings - let's keep them as singletons;
    // no need to create an instance each time.
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
        // TODO(kumpel): I cannot find the IP, but if it would be in strings,
        //               it's the how it should be obtained
        if (loginIpAddress == null) {
            loginIpAddress = this.application.getString(0);
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

    @SuppressWarnings("unchecked")
    public <V extends Contract.View> BasePresenter<V> getPresenter(V injector) {
        if (injector instanceof SignUpView) {
            // ugly, but it'll work
            return (BasePresenter<V>) getSingUpPresenter();
        } else if (injector instanceof LoginView) {
            return (BasePresenter<V>) getLoginPresenter();
        }

        throw new IllegalArgumentException("no presenter for such a view");
    }
}
