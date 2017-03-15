package project.com.ringsureapp.Service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by pankaj on 6/3/17.
 */

public class TimerService extends Service{


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
