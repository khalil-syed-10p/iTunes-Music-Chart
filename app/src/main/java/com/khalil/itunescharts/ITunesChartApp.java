package com.khalil.itunescharts;

import android.app.Application;

import com.khalil.itunescharts.network.ServiceFactory;
import com.khalil.itunescharts.network.ServiceProtocol;

public class ITunesChartApp extends Application {

    private ServiceFactory serviceFactory;
    private static ITunesChartApp instance;

    @Override
    public void onCreate() {
        instance = this;
        super.onCreate();
    }

    public static ITunesChartApp getInstance() {
        return instance;
    }

    private void initServiceFactory() {
        serviceFactory = new ServiceFactory(new ServiceProtocol());
    }

    public ServiceFactory getServiceFactory() {
        if(serviceFactory == null) {
            initServiceFactory();
        }
        return serviceFactory;
    }
}
