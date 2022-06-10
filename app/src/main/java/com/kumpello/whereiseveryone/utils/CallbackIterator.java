package com.kumpello.whereiseveryone.utils;

public interface CallbackIterator<T> {
    void onNext(T result);

    void onError(Throwable error);
}
