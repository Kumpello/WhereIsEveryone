package com.kumpello.whereiseveryone.view;

import androidx.annotation.StringRes;

import com.kumpello.whereiseveryone.mvp.Contract;

public interface SignUpView extends Contract.View {
    void showProgress();

    void showError(@StringRes int string);

    void showSuccess();
}
