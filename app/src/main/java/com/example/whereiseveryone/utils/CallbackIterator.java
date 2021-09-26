package com.example.whereiseveryone.utils;

public interface CallbackIterator<T> {
    void onNext(T result);

    void onError(Throwable error);
}
