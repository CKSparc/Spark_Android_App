package com.example.michael.spark;

import android.app.Application;

import com.parse.Parse;

/**
 * Created by Michael on 6/3/2015.
 */

/**
 * How the application is connected to the cloud database
 */
public class SparkApplication extends Application{

    @Override
    public void onCreate() {
        super.onCreate();
//        // Enable Local Datastore.
//        Parse.enableLocalDatastore(this);

        Parse.initialize(this, "W0YyTlPEZ39AtsFY2rkzAhyDuhPf7jnLUOqrrOSN", "od5J52dEcXjN4cJamK45lUez7ntslzTfr2Nsxd8j");

    }
}
