package com.kumpello.whereiseveryone;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.app.Application;
import android.content.SharedPreferences;
import android.content.res.Resources;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kumpello.whereiseveryone.model.FriendsServiceImpl;
import com.kumpello.whereiseveryone.model.LoginService;
import com.kumpello.whereiseveryone.model.LoginServiceImpl;
import com.kumpello.whereiseveryone.model.MapService;
import com.kumpello.whereiseveryone.model.MapServiceImpl;
import com.kumpello.whereiseveryone.model.PermissionHandler;
import com.kumpello.whereiseveryone.model.PermissionHandlerImpl;
import com.kumpello.whereiseveryone.model.UserService;
import com.kumpello.whereiseveryone.model.UserServiceImpl;
import com.kumpello.whereiseveryone.mvp.BasePresenter;
import com.kumpello.whereiseveryone.mvp.Contract;
import com.kumpello.whereiseveryone.presenter.FriendsPresenter;
import com.kumpello.whereiseveryone.presenter.FriendsPresenterImpl;
import com.kumpello.whereiseveryone.presenter.LoginPresenter;
import com.kumpello.whereiseveryone.presenter.LoginPresenterImpl;
import com.kumpello.whereiseveryone.presenter.MainPresenter;
import com.kumpello.whereiseveryone.presenter.MapPresenter;
import com.kumpello.whereiseveryone.presenter.MapPresenterImpl;
import com.kumpello.whereiseveryone.presenter.SignUpPresenter;
import com.kumpello.whereiseveryone.presenter.SignUpPresenterImpl;
import com.kumpello.whereiseveryone.utils.SimpleTimer;
import com.kumpello.whereiseveryone.view.FriendsView;
import com.kumpello.whereiseveryone.view.LoginView;
import com.kumpello.whereiseveryone.view.MainView;
import com.kumpello.whereiseveryone.view.MapView;
import com.kumpello.whereiseveryone.view.SignUpView;

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
        return new FriendsPresenterImpl(new FriendsServiceImpl(databaseRef, prefs, resources), new UserServiceImpl(databaseReference, prefs, resources));
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
