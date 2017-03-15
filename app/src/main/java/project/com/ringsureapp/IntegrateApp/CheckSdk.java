package project.com.ringsureapp.IntegrateApp;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

/**
 * Created by dyna11 on 23-01-2016.
 */
public class CheckSdk {

    public static float getAPIVerison() {

        Float f = null;
        try {
            StringBuilder strBuild = new StringBuilder();
            strBuild.append(android.os.Build.VERSION.RELEASE.substring(0, 2));
            f = new Float(strBuild.toString());
        } catch (NumberFormatException e) {
            Log.e("", "error retriving api version" + e.getMessage());
        }

        return f.floatValue();
    }


    public static boolean checkPermission(String strPermission,Context _c){
        int result = ContextCompat.checkSelfPermission(_c, strPermission);
        if (result == PackageManager.PERMISSION_GRANTED){

            return true;

        } else {

            return false;

        }
    }

    public static void requestPermission(String strPermission,int perCode,Context _c,Activity _a){


            if (ActivityCompat.shouldShowRequestPermissionRationale(_a, strPermission)){
                ActivityCompat.requestPermissions(_a, new String[]{strPermission}, perCode);
            } else {

                ActivityCompat.requestPermissions(_a, new String[]{strPermission}, perCode);
            }

    }

}
