package com.khalil.itunescharts.network;

public interface ServiceCallback<E> {

    void onSuccess(E response, int code);

    void onFailure(String errorMessage, int code);
}
