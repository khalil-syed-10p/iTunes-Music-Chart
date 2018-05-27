package com.khalil.itunescharts.network;

import android.text.TextUtils;
import android.view.View;

import java.io.IOException;
import java.util.concurrent.Executor;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created on 27/01/2016.
 */
@SuppressWarnings({"MethodOnlyUsedFromInnerClass"})
final class ServiceCallAdapter<E> implements ServiceCall<E> {

    private final Call<E> call;
    private Executor callbackExecutor;
    private CommonNetworkActionsHandler commonNetworkActionsHandler;
    private ServiceConfiguration serviceConfiguration;
    private ServiceProtocol serviceProtocol;
    private NetworkUI networkUI;

    ServiceCallAdapter(Call<E> call, Executor callbackExecutor, ServiceProtocol serviceProtocol) {
        this.call = call;
        this.callbackExecutor = callbackExecutor;
        this.serviceProtocol = serviceProtocol;
    }

    @Override
    public void cancel() {
        if(call == null){
            return;
        }
        call.cancel();
    }

    @SuppressWarnings({"unchecked", "AccessingNonPublicFieldOfAnotherObject"})
    @Override
    public ServiceCall<E> clone() {
        ServiceCallAdapter callAdapter = new ServiceCallAdapter<>(call.clone(), callbackExecutor, serviceProtocol);
        callAdapter.commonNetworkActionsHandler = this.commonNetworkActionsHandler;
        callAdapter.serviceConfiguration = this.serviceConfiguration;
        return callAdapter;
    }

    @Override
    public ServiceCall<E> commonNetworkActionsHandler(CommonNetworkActionsHandler commonNetworkActionsHandler) {
        this.commonNetworkActionsHandler = commonNetworkActionsHandler;
        return this;
    }

    @Override
    public ServiceCall<E> serviceConfiguration(ServiceConfiguration serviceConfiguration) {
        this.serviceConfiguration = serviceConfiguration;
        return this;
    }

    @Override
    public ServiceCall<E> networkUI(NetworkUI networkUI) {
        this.networkUI = networkUI;
        return this;
    }

    @Override
    public boolean isExecuted() {
        return (call != null) && call.isExecuted();
    }

    @Override
    public void enqueue(final ServiceCallback<E> callback) {

        willStartCall();
        call.enqueue(new Callback<E>() {

            @Override
            public void onResponse(Call<E> call, Response<E> response) {
                ServiceCallAdapter.this.onResponse(callback, call, response);
            }

            @Override
            public void onFailure(Call<E> call, Throwable t) {
                ServiceCallAdapter.this.onFailure(callback, call, t);
            }
        });
    }

    private void onResponse(ServiceCallback<E> callback, Call<E> call, Response<E> response) {
        if(call.isCanceled()) {
            didFinishCall(false);
            return;
        }
        int code = getStatusCode(response);
        if ( isUnauthorized(code) ) {
            onUnauthorized(getErrorMessage(response, code), code);
            didFinishCall(false);
            return;
        }

        try {
            if(isFailure(response)) {
                failure(callback, getErrorMessage(response, code), code);
                return;
            }
            success(callback, response.body(), code);

        } catch (Exception ex) {
            failure(callback, "", -1);
        }
    }


    private void onFailure(ServiceCallback callback, Call<E> call, Throwable t) {
        if(call.isCanceled()) {
            didFinishCall(false);
            return;
        }
        if(t instanceof IOException) {
            onInternetConnectionError();
            didFinishCall(false);
            return;
        }
        failure(callback, t.getMessage(), -1);
    }

    private void success(final ServiceCallback callback, final Object response, final int code) {
        callbackExecutor.execute(new Runnable() {
            @Override
            public void run() {
                callback.onSuccess(response, code);
            }
        });
        didFinishCall(true);
    }


    private void failure(final ServiceCallback callback, String errorMessage, final int code) {
        if(TextUtils.isEmpty(errorMessage)) {
            errorMessage = "Unknown Error";
        }
        final String finalErrorMessage = errorMessage;
        callbackExecutor.execute(new Runnable() {
            @Override
            public void run() {
                callback.onFailure(finalErrorMessage, code);
            }
        });
        didFinishCall(false);
    }

    private boolean isFailure(Response response) {
        return serviceConfiguration != null ? !serviceConfiguration.isSuccessful(response) :
                                                !serviceProtocol.isSuccessful(response);
    }

    private void onInternetConnectionError() {
        if(callbackExecutor == null) {
            return;
        }
        callbackExecutor.execute(new Runnable() {
            @Override
            public void run() {
                if(commonNetworkActionsHandler != null) {
                    commonNetworkActionsHandler.onInternetConnectionError(ServiceCallAdapter.this, getContentView());
                    return;
                }

                serviceProtocol.onInternetConnectionError(ServiceCallAdapter.this, getContentView());
            }
        });
    }

    private View getContentView(){
        return networkUI != null ? networkUI.getContentView() : null;
    }

    private int getStatusCode(Response response) {
        return serviceConfiguration != null ? serviceConfiguration.getStatusCode(response) :
                                                    serviceProtocol.getStatusCode(response);
    }

    private boolean isUnauthorized(int code) {
        return serviceConfiguration != null ? !serviceConfiguration.isAuthorized(code) :
                                                    !serviceProtocol.isAuthorized(code);
    }

    private void onUnauthorized(final String errorMessage, final int code) {

        if(callbackExecutor == null) {
            return;
        }
        callbackExecutor.execute(new Runnable() {
            @Override
            public void run() {
                if(commonNetworkActionsHandler != null) {
                    commonNetworkActionsHandler.onUnAuthorized(ServiceCallAdapter.this, errorMessage, code, getContentView());
                    return;
                }

                serviceProtocol.onUnAuthorized(ServiceCallAdapter.this, errorMessage, code, getContentView());
            }
        });
    }

    private String getErrorMessage(Response response, int code) {
        return serviceConfiguration != null ? serviceConfiguration.getErrorMessage(response, code) :
                                                serviceProtocol.getErrorMessage(response, code);
    }

    private void willStartCall() {
        if(callbackExecutor == null) {
            return;
        }
        callbackExecutor.execute(new Runnable() {
            @Override
            public void run() {
                if (networkUI != null) networkUI.willStartCall();
            }
        });
    }

    private void didFinishCall(final boolean success) {
        if(callbackExecutor == null) {
            return;
        }
        callbackExecutor.execute(new Runnable() {
            @Override
            public void run() {
                if (networkUI != null) networkUI.didFinishCall(success);
            }
        });
    }

}
