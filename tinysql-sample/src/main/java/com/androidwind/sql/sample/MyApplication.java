package com.androidwind.sql.sample;

import android.app.Application;

import com.facebook.stetho.Stetho;

/**
 * @author ddnosh
 * @website http://blog.csdn.net/ddnosh
 */
public class MyApplication extends Application {

    private static MyApplication INSTANCE;

    public void onCreate() {
        super.onCreate();
        Stetho.initializeWithDefaults(this);
        if (INSTANCE == null) {
            INSTANCE = this;
        }
    }

    public static synchronized MyApplication getInstance() {
        return INSTANCE;
    }
}
