package com.johnnymolina.imgurworkout;

import android.app.Application;
import android.content.Context;

import com.squareup.leakcanary.LeakCanary;

/**
 * Created by Johnny Molina on 3/27/2015.
 */

//NEVER USE THIS CLASS. ONLY USE IF IN DIRE NEED & CANT FIGURE OUT HOW TO GET CONTEXT TO CONTINUE IMPLEMENTING A FEATURE. CAUSES OUTOFMEMORY EXCEPTIONS> MANY LEAKS.
public class MyApplication extends Application {

    private static Context context;

    public void onCreate(){
        super.onCreate();
        MyApplication.context = getApplicationContext();
        LeakCanary.install(this);
    }

    public static Context getAppContext() {
        return MyApplication.context;
    }
}
