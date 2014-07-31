
package com.cts.feedviewer.app;

import android.app.Application;
import android.content.Context;

/**
 * This class provide application context and works as a central entity inside our process.
 * We can use this class to set any central data as well which can work as central bean.
 * @author 330016
 *
 */
public class FeedApplication extends Application {
    private static Context context;

    public void onCreate() {
        super.onCreate();
        FeedApplication.context = getApplicationContext();
    }

    public static Context getAppContext() {
        return FeedApplication.context;
    }
}
