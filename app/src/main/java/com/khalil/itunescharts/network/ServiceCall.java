package com.khalil.itunescharts.network;

/**
 * Created on 27/01/2016.
 */
public interface ServiceCall<E> {

    void cancel();
    void enqueue (ServiceCallback<E> callback);

    ServiceCall<E> clone();
    ServiceCall<E> commonNetworkActionsHandler(CommonNetworkActionsHandler commonNetworkActionsHandler);
    ServiceCall<E> serviceConfiguration(ServiceConfiguration serviceConfiguration);
    ServiceCall<E> networkUI(NetworkUI networkUI);
    boolean isExecuted();
}
