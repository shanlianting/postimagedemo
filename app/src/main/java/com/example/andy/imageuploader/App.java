package com.example.andy.imageuploader;

import android.app.Application;

import org.xutils.x;


public class App extends Application{
    @Override
    public void onCreate() {
        super.onCreate();
        x.Ext.init(this);
        x.Ext.setDebug(BuildConfig.DEBUG);
    }
}
