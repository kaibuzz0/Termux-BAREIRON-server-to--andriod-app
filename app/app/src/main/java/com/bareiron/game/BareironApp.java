// BareironApp.java — Updated with PlayerProgress + BillingManager init
package com.bareiron.game;

import android.app.Application;
import android.content.Context;

public class BareironApp extends Application {
    private static Context appContext;
    
    @Override
    public void onCreate() {
        super.onCreate();
        appContext = getApplicationContext();
        ContentPackManager.init(appContext);
        PlayerProgress.init(appContext);
        BillingManager.init(appContext);
    }
    
    public static Context getAppContext() {
        return appContext;
    }
}
