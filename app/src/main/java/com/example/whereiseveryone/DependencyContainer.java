package com.example.whereiseveryone;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.app.Application;
import android.content.SharedPreferences;
import android.content.res.Resources;

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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.jetbrains.annotations.NotNull;

// TODO(P2): Use one of popular DependencyContainers for Android to get rid of this.

public class DependencyContainer {

    private final Application application;

    private LoginService loginService;
    private UserService userService;

    private DatabaseReference databaseReference;


    // TODO: We should have a lock for initializing.
    //       For now let's skip it.

    public DependencyContainer(Application application) {
        this.application = application;
    }

    @NonNull
    public LoginService getLoginService(Activity activity) {
        if (loginService == null) {
            loginService = new LoginServiceImpl(getResources(), activity);
        }

        return loginService;
    }

    @NonNull
    public UserService getUserService(DatabaseReference ref, SharedPreferences prefs, Resources resources) {
        if (userService == null) {
            userService = new UserServiceImpl(ref, prefs, resources);
        }

        return userService;
    }

    @NonNull
    public DatabaseReference getDatabaseReference(Activity activity) {
        if (databaseReference == null) {
            this.databaseReference = FirebaseDatabase.getInstance(
                    activity.getString(R.string.server_address)).getReference();
        }
        return databaseReference;
    }

    @NonNull
    public Resources getResources() {
        return application.getResources();
    }

    // We want to create presenters every time,
    // so we shouldn't keep any references to them
    @NonNull
    public LoginPresenter getLoginPresenter(DatabaseReference ref, SharedPreferences preferences, Activity activity) {
        return new LoginPresenterImpl(
                getLoginService(activity),
                getUserService(ref, preferences, getResources())
        );
    }

    @NonNull
    public SignUpPresenter getSingUpPresenter(Activity activity) {
        return new SignUpPresenterImpl(getLoginService(activity)
        );
    }

    @NotNull
    public MapPresenter getMapPresenter(Activity activity) {
        return new MapPresenterImpl(
                getMapService(activity),
                getPermissionHandler(activity),
                getUserService(
                        getDatabaseReference(activity),
                        getSharedPreferences(activity),
                        getResources()
                ),
                new SimpleTimer());
    }

    @NonNull
    public FriendsPresenter getFriendsPresenter(DatabaseReference databaseRef, SharedPreferences prefs, Resources resources) {
        return new FriendsPresenterImpl(new FriendsServiceImpl(databaseRef, prefs, resources));
    }

    @NonNull
    public MainPresenter getMainPresenter() {
        return new MainPresenter();
    }

    // Services etc.
    @NotNull
    public MapService getMapService(Activity activity) {
        return new MapServiceImpl(activity);
    }

    @NonNull
    public SharedPreferences getSharedPreferences(Activity activity) {
        return activity.getSharedPreferences("WhereIsEveryone", MODE_PRIVATE);
    }

    @NotNull
    public PermissionHandler getPermissionHandler(Activity activity) {
        return new PermissionHandlerImpl(activity);
    }

    @SuppressWarnings("unchecked")
    public <V extends Contract.View> BasePresenter<V> getPresenter(V injector) throws IllegalArgumentException {
        Activity activity;

        if (injector instanceof Activity) {
            activity = (Activity) injector;
        } else if (injector instanceof Fragment) {
            activity = (Activity) ((Fragment) injector).getActivity();
        } else {
            throw new IllegalArgumentException("Injector must be Activity object");
        }

        if (injector instanceof SignUpView) {
            // ugly, but it'll work
            return (BasePresenter<V>) getSingUpPresenter(activity);
        } else if (injector instanceof LoginView) {
            return (BasePresenter<V>) getLoginPresenter(
                    getDatabaseReference(activity),
                    getSharedPreferences(activity), activity
            );
        } else if (injector instanceof MapView) {
            return (BasePresenter<V>) getMapPresenter(activity);
        } else if (injector instanceof FriendsView) {
            return (BasePresenter<V>) getFriendsPresenter(
                    getDatabaseReference(activity),
                    getSharedPreferences(activity),
                    getResources()
            );
        } else if (injector instanceof MainView) {
            return (BasePresenter<V>) getMainPresenter();
        }

        throw new IllegalArgumentException("no presenter for such a view");
    }
}
