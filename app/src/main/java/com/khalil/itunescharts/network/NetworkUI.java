package com.khalil.itunescharts.network;

import android.view.View;

public interface NetworkUI {

    void willStartCall();
    void didFinishCall(boolean success);
    View getContentView();
}
