package project.com.ringsureapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by apple on 02/08/16.
 */
public class Introduction_Activity extends AppCompatActivity {

    SharedPreferences pref;
    SharedPreferences.Editor editor;
    public static String TAG = "pref";

    ImageView imgIntro;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.introduction_activity);

        pref = getSharedPreferences(TAG,MODE_PRIVATE);
        editor = pref.edit();

        imgIntro = (ImageView)findViewById(R.id.imgIntro);
        imgIntro.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                editor.putBoolean("intro",true).commit();
                Intent i = new Intent(Introduction_Activity.this, HomePage_Activity.class);
                startActivity(i);
                finish();
                return false;
            }
        });



    }
}
