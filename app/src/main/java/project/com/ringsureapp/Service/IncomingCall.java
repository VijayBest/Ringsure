package project.com.ringsureapp.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
/**
 * Created by apple on 01/08/16.
 */
public class IncomingCall extends BroadcastReceiver {
    @Override
    public void onReceive(final Context context, Intent intent) {
        try {
            Bundle bundle = intent.getExtras();
            final TelephonyManager tmgr = (TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE);
            final String phonenumber = bundle.getString(TelephonyManager.EXTRA_INCOMING_NUMBER);
            final String state = bundle.getString(TelephonyManager.EXTRA_STATE);
//        MyPhoneStateListener PhoneListener = new MyPhoneStateListener(context);
            if (state.equals("RINGING") || state.equals("1")) {
                tmgr.listen(new MyPhoneStateListener(context, phonenumber, state), PhoneStateListener.LISTEN_CALL_STATE);
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }
}
