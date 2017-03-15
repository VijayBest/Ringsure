package project.com.ringsureapp;

import android.app.Application;

import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;
import project.com.ringsureapp.AppData.FunctionUtils;

/**
 * Created by Sahil on 9/6/2016.
 */
public class RingSureApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());

        new FunctionUtils(this);

    }
}
