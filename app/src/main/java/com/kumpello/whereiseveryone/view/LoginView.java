package com.kumpello.whereiseveryone.view;

import androidx.annotation.StringRes;

import com.kumpello.whereiseveryone.mvp.Contract;

public interface LoginView extends Contract.View {
    void showError(@StringRes int errorMessage);

    void showProgress();

    void showSuccess();
}
