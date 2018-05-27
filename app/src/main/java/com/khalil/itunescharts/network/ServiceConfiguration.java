package com.khalil.itunescharts.network;

import retrofit2.Response;

public interface ServiceConfiguration {

    String getErrorMessage (Response response, int code);

    int getStatusCode (Response response);

    boolean isSuccessful (Response response);

    boolean isAuthorized (int code);
}
