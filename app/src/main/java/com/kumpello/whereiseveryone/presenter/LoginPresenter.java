package com.kumpello.whereiseveryone.presenter;

import android.content.Intent;

import com.google.android.gms.common.ConnectionResult;
import com.kumpello.whereiseveryone.model.UserType;
import com.kumpello.whereiseveryone.mvp.Contract;
import com.kumpello.whereiseveryone.utils.OnResult;
import com.kumpello.whereiseveryone.view.LoginView;

import java.io.FileNotFoundException;

public interface LoginPresenter extends Contract.Presenter<LoginView> {
    void loginButtonClicked(String login, String password);
    void googleButtonClicked();
    void saveUserData(UserType userType, Intent intent, OnResult<ConnectionResult> result);
}
