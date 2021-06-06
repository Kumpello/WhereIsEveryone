package com.example.WhereIsEveryone.view;

import androidx.annotation.StringRes;

public interface SignUpView {
    void showProgress();

    void showError(@StringRes int string);

    void showSuccess();
}
