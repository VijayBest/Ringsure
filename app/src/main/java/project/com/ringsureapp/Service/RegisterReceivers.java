package project.com.ringsureapp.Service;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by apple on 02/08/16.
 */
public class RegisterReceivers extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        IntentFilter filter = new IntentFilter("android.intent.action.PHONE_STATE");
        IncomingCall myReceiver = new IncomingCall();
        registerReceiver(myReceiver, filter);
        stopSelf();
    }
}
