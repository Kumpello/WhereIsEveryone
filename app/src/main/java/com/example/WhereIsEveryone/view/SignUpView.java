package com.example.WhereIsEveryone.view;

import androidx.annotation.StringRes;

import com.example.WhereIsEveryone.mvp.Contract;

public interface SignUpView extends Contract.View {
    void showProgress();

    void showError(@StringRes int string);

    void showSuccess();
}
