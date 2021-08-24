package com.example.whereiseveryone;

import android.app.Activity;
import android.app.Application;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.whereiseveryone.model.FriendsServiceImpl;
import com.example.whereiseveryone.model.LoginService;
import com.example.whereiseveryone.model.LoginServiceImpl;
import com.example.whereiseveryone.model.MapService;
import com.example.whereiseveryone.model.MapServiceImpl;
import com.example.whereiseveryone.model.PermissionHandler;
import com.example.whereiseveryone.model.PermissionHandlerImpl;
import com.example.whereiseveryone.model.UserService;
import com.example.whereiseveryone.model.UserServiceImpl;
import com.example.whereiseveryone.mvp.BasePresenter;
import com.example.whereiseveryone.mvp.Contract;
import com.example.whereiseveryone.presenter.FriendsPresenter;
import com.example.whereiseveryone.presenter.FriendsPresenterImpl;
import com.example.whereiseveryone.presenter.LoginPresenter;
import com.example.whereiseveryone.presenter.LoginPresenterImpl;
import com.example.whereiseveryone.presenter.MainPresenter;
import com.example.whereiseveryone.presenter.MapPresenter;
import com.example.whereiseveryone.presenter.MapPresenterImpl;
import com.example.whereiseveryone.presenter.SignUpPresenter;
import com.example.whereiseveryone.presenter.SignUpPresenterImpl;
import com.example.whereiseveryone.utils.SimpleTimer;
import com.example.whereiseveryone.view.FriendsView;
import com.example.whereiseveryone.view.LoginView;
import com.example.whereiseveryone.view.MainView;
import com.example.whereiseveryone.view.MapView;
import com.example.whereiseveryone.view.SignUpView;

import org.jetbrains.annotations.NotNull;


public class DependencyContainer {

    private final Application application;

    private LoginService loginService;
    private UserService userService;



    // TODO: We should have a lock for initializing.
    //       For now let's skip it.

    public DependencyContainer(Application application) {
        this.application = application;
    }

    @NonNull
    public LoginService getLoginService() {
        if (loginService == null) {
            loginService = new LoginServiceImpl();
        }

        return loginService;
    }

    @NonNull
    public UserService getUserService(Activity activity) {
        if (userService == null) {
            userService = new UserServiceImpl(activity);
        }

        return userService;
    }


    // We want to create presenters every time,
    // so we shouldn't keep any references to them
    @NonNull
    public LoginPresenter getLoginPresenter(Activity activity) {
        return new LoginPresenterImpl(
                getLoginService(),
                getUserService(activity)
        );
    }

    @NonNull
    public SignUpPresenter getSingUpPresenter() {
        return new SignUpPresenterImpl(getLoginService()
        );
    }

    @NotNull
    public MapPresenter getMapPresenter(Activity activity) {
        return new MapPresenterImpl(getMapService(activity), getPermissionHandler(activity), getUserService(activity), new SimpleTimer());
    }

    @NonNull
    public FriendsPresenter getFriendsPresenter(Activity activity) {
        return new FriendsPresenterImpl(new FriendsServiceImpl(activity));
    }

    @NonNull
    public MainPresenter getMainPresenter() { return new MainPresenter(); }
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
        }else if(injector instanceof Fragment) {
            activity = (Activity) ((Fragment) injector).getActivity();
        }else {
            throw new IllegalArgumentException("Injector must be Activity object");
        }

        if (injector instanceof SignUpView) {
            // ugly, but it'll work
            return (BasePresenter<V>) getSingUpPresenter();
        } else if (injector instanceof LoginView) {
            return (BasePresenter<V>) getLoginPresenter(activity);
        } else if (injector instanceof MapView) {
            return (BasePresenter<V>) getMapPresenter(activity);
        } else if (injector instanceof FriendsView) {
            return (BasePresenter<V>) getFriendsPresenter(activity);
        } else if (injector instanceof MainView) {
            return (BasePresenter<V>) getMainPresenter();
        }

        throw new IllegalArgumentException("no presenter for such a view");
    }
}
