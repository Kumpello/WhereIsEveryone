package com.example.whereiseveryone.view;

import androidx.annotation.StringRes;

import com.example.whereiseveryone.mvp.Contract;

public interface SignUpView extends Contract.View {
    void showProgress();

    void showError(@StringRes int string);

    void showSuccess();
}
