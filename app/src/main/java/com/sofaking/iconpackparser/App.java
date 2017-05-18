package com.sofaking.iconpackparser;

import android.app.Application;

import com.sofaking.iconpack.IconPackManager;

/**
 * Created by nadavfima on 18/05/2017.
 */

public class App extends Application {


    private IconPackManager mIconPackManager;

    @Override
    public void onCreate() {
        super.onCreate();


        mIconPackManager = new IconPackManager();
    }

    public IconPackManager getIconPackManager() {
        return mIconPackManager;
    }

}
