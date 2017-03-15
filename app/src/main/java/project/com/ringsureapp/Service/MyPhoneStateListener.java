package project.com.ringsureapp.Service;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import java.io.IOException;

import project.com.ringsureapp.DataBase.MyDatabase;

/**
 * Created by apple on 01/08/16.
 */
public class MyPhoneStateListener extends PhoneStateListener {

    Context context;
    int callstart = 0;
    MediaPlayer mp;

    SharedPreferences pref;
    SharedPreferences.Editor editor;
    public static String TAG = "pref";

    MyDatabase dbHelper;
    String number;
    String state;
    int states;

    public MyPhoneStateListener(Context context, String number, String state) {
        this.context = context;
        this.number = number;
        this.state = state;

        if (state.equals("RINGING") || state.equals("1")) {
            states = 1;
        } else if (state.equals("IDLE") || state.equals("0")) {
            states = 0;
        }

        pref = context.getSharedPreferences(TAG, context.MODE_PRIVATE);
        editor = pref.edit();

        dbHelper = new MyDatabase(context);
    }

    public void onCallStateChanged(int state, String incomingNumber) {

        super.onCallStateChanged(state, incomingNumber);
        String finalPhone;
        String finalPhone1;

        String phone2 = number.replaceAll(" ", "");
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

        AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

        if (callstart == 1) {
            states = 0;
        }

        switch (states) {
            case TelephonyManager.CALL_STATE_IDLE:
                // CALL_STATE_IDLE;
                am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
//                    Toast.makeText(getApplicationContext(), "CALL_STATE_IDLE",
//                            Toast.LENGTH_LONG).show();
                if (callstart == 1) {
                    if (mp != null) {
                        mp.stop();
                        mp.release();
                        mp = null;
                    }

                    if (pref.getString("MODE", "").equals("Silent")) {
                        am.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                    } else if (pref.getString("MODE", "").equals("Vibrate")) {
                        am.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
                    } else {
                        am.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                    }
                    callstart = 0;
                }
//				am.setRingerMode(AudioManager.RINGER_MODE_SILENT);
//				am.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
                break;
            case TelephonyManager.CALL_STATE_OFFHOOK:
                // CALL_STATE_OFFHOOK;
//                    Toast.makeText(getApplicationContext(), "CALL_STATE_OFFHOOK",
//                            Toast.LENGTH_LONG).show();
                break;
            case TelephonyManager.CALL_STATE_RINGING:
                // CALL_STATE_RINGING
//				AudioManager am1 = (AudioManager) getBaseContext().getSystemService(Context.AUDIO_SERVICE);
                int a = am.getRingerMode();
                if (a == 0) {

                    editor.putString("MODE", "Silent").commit();

                } else if (a == 1) {
                    editor.putString("MODE", "Vibrate").commit();


                } else if (a == 2) {
                    editor.putString("MODE", "Normal").commit();

                }

                if (a == 2) {
                    callstart = 1;

                    if (pref.getString("MODE", "").equals("Silent")) {
                        final String mode = dbHelper.getDataFromNumberSilent1(finalPhone1);

//                        Toast.makeText(getApplicationContext(),  finalPhone1 + "   " + mode, Toast.LENGTH_SHORT).show();

                        if (mode.equals("Silent")) {
                            am.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                        } else if (mode.equals("General")) {
                            am.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                        } else {
                            am.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                        }

                    } else if (pref.getString("MODE", "").equals("Vibrate")) {

                    } else if (pref.getString("MODE", "").equals("Normal")) {
                        final String mode = dbHelper.getDataFromNumber1(finalPhone1);
                        if (mode.equals("Silent")) {
                            am.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                        } else if (mode.equals("General")) {
                            am.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                        } else {
                            am.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                        }
                    }
                }

                if (a == 0) {
                    callstart = 1;

                    if (pref.getString("MODE", "").equals("Silent")) {
                        final String mode = dbHelper.getDataFromNumberSilent1(finalPhone1);

                        if (mode.equals("Silent")) {
                            am.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                        } else if (mode.equals("General")) {
                            am.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                            mp = new MediaPlayer();
                            try {
                                AssetFileDescriptor afd = context.getAssets().openFd("ring.mp3");
                                mp.setDataSource(
                                        afd.getFileDescriptor(),
                                        afd.getStartOffset(),
                                        afd.getLength()
                                );
                                afd.close();

//                                    mp.setDataSource(Environment.getExternalStorageDirectory().getPath()+"/Music/ring.mp3");
                                mp.prepare();
                                mp.start();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            am.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                        }
                    } else if (pref.getString("MODE", "").equals("Vibrate")) {

                    } else if (pref.getString("MODE", "").equals("Normal")) {
                        final String mode = dbHelper.getDataFromNumber1(finalPhone1);
                        if (mode.equals("Silent")) {
                            am.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                        } else if (mode.equals("General")) {
                            am.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                        } else {
                            am.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                        }
                    }
                }

                if (a == 1) {
                    callstart = 1;

                    if (pref.getString("MODE", "").equals("Vibrate")) {
                        final String mode = dbHelper.getDataFromNumberSilent1(finalPhone1);

                        if (mode.equals("Silent")) {
                            am.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                        } else if (mode.equals("General")) {
                            am.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                            mp = new MediaPlayer();
                            try {
                                AssetFileDescriptor afd = context.getAssets().openFd("ring.mp3");
                                mp.setDataSource(
                                        afd.getFileDescriptor(),
                                        afd.getStartOffset(),
                                        afd.getLength()
                                );
                                afd.close();

//                                    mp.setDataSource(Environment.getExternalStorageDirectory().getPath()+"/Music/ring.mp3");
                                mp.prepare();
                                mp.start();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            am.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
                        }
                    } else if (pref.getString("MODE", "").equals("Vibrate")) {

                    } else if (pref.getString("MODE", "").equals("Normal")) {
                        final String mode = dbHelper.getDataFromNumber1(finalPhone1);
                        if (mode.equals("Silent")) {
                            am.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                        } else if (mode.equals("General")) {
                            am.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                        } else {
                            am.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                        }
                    }
                }
                // Toast.makeText(getApplicationContext(), "CALL_STATE_RINGING",
                // Toast.LENGTH_LONG).show();

                break;
            default:
                break;
        }
    }

}
