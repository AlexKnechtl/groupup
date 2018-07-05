package com.example.alexander.groupup.Helpers;

public interface OnGetResultListener<T> {
    void OnSuccess(T value);
    void OnFail();
}
