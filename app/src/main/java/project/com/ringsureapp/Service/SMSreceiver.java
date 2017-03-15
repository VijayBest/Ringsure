package project.com.ringsureapp.Service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.SmsMessage;
import android.util.Log;

import project.com.ringsureapp.DataBase.MyDatabase;

/**
 * Created by apple on 14/07/16.
 */
public class SMSreceiver extends BroadcastReceiver {
    String finalPhone = "",finalPhone1 = "";
    MyDatabase dbHelper;

    SharedPreferences pref;
    SharedPreferences.Editor editor;
    public static String TAG = "pref";
    AudioManager am;
    MediaPlayer mp;
    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        dbHelper = new MyDatabase(context);
        pref = context.getSharedPreferences(TAG, context.MODE_PRIVATE);
        editor = pref.edit();

        try {

            if (bundle != null) {

                final Object[] pdusObj = (Object[]) bundle.get("pdus");

                for (int i = 0; i < pdusObj.length; i++) {

                    SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                    String phoneNumber = currentMessage.getDisplayOriginatingAddress();

                    if(!phoneNumber.equals("")) {
                        String phone2 = phoneNumber.replaceAll(" ", "");
                        if (phone2.contains("+91")) {
                            finalPhone = phone2.replace("+91", "0");
                        } else if (phone2.contains("-")) {
                            finalPhone = phone2.replaceAll("-", "");
                        } else {
                            finalPhone = phone2;
                        }

                        String num = finalPhone.replace("(", "");
                        String num1 = num.replace(")", "");

                        if (num1.trim().length() < 11) {
                            finalPhone1 = "0" + num1;
                        } else {
                            finalPhone1 = num1;
                        }
                    }

                    am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                    int a = am.getRingerMode();
                    if (a == 0) {

                        editor.putString("SMSMODE", "Silent").commit();

                    } else if (a == 1) {
                        editor.putString("SMSMODE", "Vibrate").commit();

                    } else if (a == 2) {
                        editor.putString("SMSMODE", "Normal").commit();
                    }

                    if(pref.getString("SMSMODE", "").equals("Normal")){

                        final String mode = dbHelper.getDataFromNumber2(finalPhone1);
                        if (mode.equals("Silent")) {
                            am.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                        } else if(mode.equals("General")) {
                            am.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                        }else{
                            am.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                        }

                        Handler h = new Handler();
                        h.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (pref.getString("SMSMODE", "").equals("Silent")) {
                                    am.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                                } else if (pref.getString("SMSMODE", "").equals("Vibrate")) {
                                    am.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
                                } else {
                                    am.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                                }
                            }
                        },12000);
                    }

                    if(pref.getString("SMSMODE", "").equals("Silent")){
                        final String mode = dbHelper.getDataFromNumberSilent2(finalPhone1);
                        if (mode.equals("Silent")) {
                            am.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                        } else if(mode.equals("General")) {
                            am.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
//                            mp = new MediaPlayer();
//                            try {
//                                AssetFileDescriptor afd = context.getAssets().openFd("smsring.mp3");
//                                mp.setDataSource(
//                                        afd.getFileDescriptor(),
//                                        afd.getStartOffset(),
//                                        afd.getLength()
//                                );
//                                afd.close();
//
////                                    mp.setDataSource(Environment.getExternalStorageDirectory().getPath()+"/Music/ring.mp3");
//                                mp.prepare();
//                                mp.start();
//                            } catch (IOException e) {
//                                e.printStackTrace();
//                            }
                        }else{
                            am.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                        }

                        Handler h = new Handler();
                        h.postDelayed(new Runnable() {
                            @Override
                            public void run() {
//                                if(mp != null){
//                                    mp.stop();
//                                    mp.release();
//                                    mp = null;
//                                }
                                if (pref.getString("SMSMODE", "").equals("Silent")) {
                                    am.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                                } else if (pref.getString("SMSMODE", "").equals("Vibrate")) {
                                    am.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
                                } else {
                                    am.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                                }
                            }
                        },12000);
                    }


                    if(pref.getString("SMSMODE", "").equals("Vibrate")){
                        final String mode = dbHelper.getDataFromNumberSilent2(finalPhone1);
                        if (mode.equals("Silent")) {
                            am.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                        } else if(mode.equals("General")) {
                            am.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
//                            mp = new MediaPlayer();
//                            try {
//                                AssetFileDescriptor afd = context.getAssets().openFd("smsring.mp3");
//                                mp.setDataSource(
//                                        afd.getFileDescriptor(),
//                                        afd.getStartOffset(),
//                                        afd.getLength()
//                                );
//                                afd.close();
//
////                                    mp.setDataSource(Environment.getExternalStorageDirectory().getPath()+"/Music/ring.mp3");
//                                mp.prepare();
//                                mp.start();
//                            } catch (IOException e) {
//                                e.printStackTrace();
//                            }
                        }else{
                            am.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
                        }

                        Handler h = new Handler();
                        h.postDelayed(new Runnable() {
                            @Override
                            public void run() {
//                                if(mp != null){
//                                    mp.stop();
//                                    mp.release();
//                                    mp = null;
//                                }
                                if (pref.getString("SMSMODE", "").equals("Silent")) {
                                    am.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                                } else if (pref.getString("SMSMODE", "").equals("Vibrate")) {
                                    am.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
                                } else {
                                    am.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                                }
                            }
                        },12000);
                    }


                } // end for loop
            } // bundle is null

        } catch (Exception e) {
            Log.e("SmsReceiver", "Exception smsReceiver" + e);

        }


    }
}
