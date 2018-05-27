package com.khalil.itunescharts.activities.base;

import android.support.v7.app.AppCompatActivity;

import com.khalil.itunescharts.R;
import com.khalil.itunescharts.ITunesChartApp;
import com.khalil.itunescharts.components.Loader;
import com.khalil.itunescharts.network.NetworkUI;
import com.khalil.itunescharts.network.ServiceFactory;

public abstract class BaseActivity extends AppCompatActivity implements NetworkUI {

    @Override
    public void willStartCall() {
        Loader.showLoader(this,getString(R.string.text_please_wait),"");
    }

    @Override
    public void didFinishCall(boolean success) {
        Loader.hideLoader();
    }

    protected ServiceFactory getServiceFactory() {
        return ITunesChartApp.getInstance().getServiceFactory();
    }
}
