package com.khalil.itunescharts.network;

import android.content.Context;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.view.View;

import com.khalil.itunescharts.ITunesChartApp;
import com.khalil.itunescharts.R;

import java.io.IOException;

import okhttp3.Request;
import retrofit2.Response;

public class ServiceProtocol implements ServiceConfiguration, CommonNetworkActionsHandler {

    public String getAPIURL() {
        return getContext().getString(R.string.api_url);
    }

    public Context getContext() {
        return ITunesChartApp.getInstance();
    }

    public Request.Builder addHeaders(Request.Builder requestBuilder) {
        return requestBuilder;
    }

    @Override
    public void onUnAuthorized(ServiceCall serviceCall, String errorMessage, int code, View contentView) {

        showSnackbar(contentView, R.string.error_unauthorized);
    }

    @Override
    public void onInternetConnectionError(ServiceCall serviceCall, View contentView) {

        showSnackbar(contentView, R.string.error_internet_connection);
    }

    private void showSnackbar(final View contentView, @StringRes int message) {
        if (contentView == null) {
            return;
        }

        try {
            final Snackbar snackbar = Snackbar.make(contentView, message, Snackbar.LENGTH_SHORT);
            snackbar.show ();
            contentView.setOnClickListener (new View.OnClickListener () {
                @Override
                public void onClick (View v) {

                    snackbar.dismiss ();
                    contentView.setOnClickListener (null);
                }
            });
        }
        catch (Exception ex) {
            ex.printStackTrace ();
        }
    }

    @Override
    public String getErrorMessage(Response response, int code) {
        try {

            return response.errorBody ().string ();
        }
        catch (IOException ex) {
            return "";
        }
    }

    @Override
    public int getStatusCode(Response response) {

        if(response == null) {
            return -1;
        }
        return response.code ();
    }

    @Override
    public boolean isSuccessful(Response response) {
        return response != null && response.errorBody () == null;
    }

    @Override
    public boolean isAuthorized(int code) {
        return code != 401;
    }
}
