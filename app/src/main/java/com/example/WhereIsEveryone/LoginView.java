package com.example.WhereIsEveryone;

import androidx.annotation.StringRes;

public interface LoginView {
    void showError(@StringRes int errorMessage);
    void showError(String error);
    void showProgress();
    void showSuccess();
}
