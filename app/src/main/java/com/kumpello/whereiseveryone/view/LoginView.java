package com.kumpello.whereiseveryone.view;

import android.content.Intent;

import androidx.annotation.StringRes;

import com.kumpello.whereiseveryone.mvp.Contract;

public interface LoginView extends Contract.View {
    void showError(@StringRes int errorMessage);

    void showProgress();

    void showSuccess();

    void loginByGoogle(Intent intent);
}
