package com.example.WhereIsEveryone.view;

import androidx.annotation.StringRes;

import com.example.WhereIsEveryone.mvp.Contract;

public interface LoginView extends Contract.View {
    void showError(@StringRes int errorMessage);

    void showProgress();

    void showSuccess();
}
