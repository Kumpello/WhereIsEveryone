package com.example.whereiseveryone.utils;

public interface OnResult<T> {
    void onSuccess(T result);

    void onError(Throwable error);
}
