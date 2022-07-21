package com.kumpello.whereiseveryone.utils;

public interface OnResult<T> {
    void onSuccess(T result);

    void onError(Throwable error);
}
