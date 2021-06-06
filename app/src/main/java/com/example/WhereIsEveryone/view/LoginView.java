package com.example.WhereIsEveryone.view;

import androidx.annotation.StringRes;

public interface LoginView {
    void showError(@StringRes int errorMessage);
    void showProgress();
    void showSuccess();
}
