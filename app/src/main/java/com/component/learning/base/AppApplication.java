package com.component.learning.base;

import android.app.Application;
import android.content.Context;

public class AppApplication extends Application {

    private static Context mContext;
    private static AppApplication app;

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
        mContext = this;
    }

    public static AppApplication getApp() {
        return app;
    }

    public static Context getContext() {
        return mContext;
    }
}
