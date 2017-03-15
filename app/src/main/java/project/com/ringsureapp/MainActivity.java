package project.com.ringsureapp;
import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import project.com.ringsureapp.DataBase.MyDatabase;
import project.com.ringsureapp.IntegrateApp.CheckSdk;
public class MainActivity extends AppCompatActivity {
    public static final int PERMISSION_REQUEST_CODE_READ_STORAGE = 0;
    public static final int PERMISSION_REQUEST_CODE_SMS = 2;
    public static final int PERMISSION_REQUEST_CODE_PHONE = 3;
    public static final int PERMISSION_REQUEST_CODE_CONTACT = 4;
    public static final int ASK_MULTIPLE_PERMISSION_REQUEST_CODE = 1;

    boolean storage,sms,phone,contact;

    SharedPreferences pref;
    SharedPreferences.Editor editor;
    public static String TAG = "pref";

    MyDatabase dbHelper;

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE_READ_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Handler h = new Handler();
                    h.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if(pref.getBoolean("intro",false)){
                                Intent i = new Intent(MainActivity.this, HomePage_Activity.class);
                                startActivity(i);
                                finish();
                            }else{
                                Intent i = new Intent(MainActivity.this, Introduction_Activity.class);
                                startActivity(i);
                                finish();
                            }
                        }
                    }, 3000);
                }else{
                    Dialog();
                }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);
        pref = getSharedPreferences(TAG,MODE_PRIVATE);
        editor = pref.edit();

        dbHelper = new MyDatabase(MainActivity.this);

       /* if(!pref.getBoolean("delete",false)){
            dbHelper.DropTable();
            editor.putBoolean("delete",true).commit();
        }*/
        float a = CheckSdk.getAPIVerison();
        if (a >= 6.0) {
            if (CheckSdk.checkPermission(Manifest.permission.READ_PHONE_STATE, getApplicationContext()) ) {
                Handler h = new Handler();
                h.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(pref.getBoolean("intro",false)){
                            Intent i = new Intent(MainActivity.this, HomePage_Activity.class);
                            startActivity(i);
                            finish();
                        }else{
                            Intent i = new Intent(MainActivity.this, Introduction_Activity.class);
                            startActivity(i);
                            finish();
                        }

                    }
                }, 3000);
            } else {
                CheckSdk.requestPermission(Manifest.permission.READ_PHONE_STATE, PERMISSION_REQUEST_CODE_READ_STORAGE, getApplicationContext(), MainActivity.this);
            }
        }else {
            Handler h = new Handler();
            h.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(pref.getBoolean("intro",false)){
                        Intent i = new Intent(MainActivity.this, HomePage_Activity.class);
                        startActivity(i);
                        finish();
                    }else{
                        Intent i = new Intent(MainActivity.this, Introduction_Activity.class);
                        startActivity(i);
                        finish();
                    }
                }
            }, 3000);
        }
    }
    public void Dialog(){
        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(R.layout.dialog_alert);

        Button btnOK = (Button)dialog.findViewById(R.id.btnOK);
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckSdk.requestPermission(Manifest.permission.READ_PHONE_STATE, PERMISSION_REQUEST_CODE_READ_STORAGE, getApplicationContext(), MainActivity.this);
                dialog.dismiss();
            }
        });
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }
}
