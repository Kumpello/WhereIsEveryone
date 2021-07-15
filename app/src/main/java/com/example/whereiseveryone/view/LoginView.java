package com.example.whereiseveryone.view;

import androidx.annotation.StringRes;

import com.example.whereiseveryone.mvp.Contract;

public interface LoginView extends Contract.View {
    void showError(@StringRes int errorMessage);

    void showProgress();

    void showSuccess();
}
