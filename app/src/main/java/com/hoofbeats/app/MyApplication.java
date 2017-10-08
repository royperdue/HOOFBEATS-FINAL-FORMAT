package com.hoofbeats.app;

import android.app.Application;
import android.content.Context;

import com.hoofbeats.app.model.DaoMaster;
import com.hoofbeats.app.model.DaoSession;

/**
 * Created by royperdue on 3/13/17.
 */
public class MyApplication extends Application
{
    private static Context context;

    private DaoSession daoSession;
    public DaoSession getDaoSession()
    {
        return daoSession;
    }
    public static Context getAppContext()
    {
        return MyApplication.context;
    }

    // Singleton instance
    private static MyApplication singleton;
    public static MyApplication getInstance(){
        return singleton;
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
        singleton = MyApplication.this;

        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(getApplicationContext(), "hoofbeats-db", null);
        DaoMaster daoMaster = new DaoMaster(helper.getWritableDatabase());
        daoSession = daoMaster.newSession();

        MyApplication.context = getApplicationContext();
    }
}
