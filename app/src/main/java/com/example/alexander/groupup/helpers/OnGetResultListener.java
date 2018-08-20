package com.example.alexander.groupup.helpers;

public interface OnGetResultListener<T> {
    void OnSuccess(T value);

    void OnFail();
}