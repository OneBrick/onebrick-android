package org.onebrick.android.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import org.onebrick.android.OneBrickApplication;
import org.onebrick.android.R;

public class SplashScreenActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch_acticty);

        final String  myChapterName = OneBrickApplication
                .getApplicationSharedPreference()
                .getString("MyChapterName", null);
        final int myChapterId = OneBrickApplication
                .getApplicationSharedPreference()
                .getInt("MyChapterId", -1);
        if(myChapterName == null) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent i = new Intent(SplashScreenActivity.this, SelectChapterActivity.class);
                    startActivity(i);
                }
            }, 2500);
        } else {
            //Toast.makeText(this,"My Chapter is "+myChapterName,Toast.LENGTH_LONG).show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent i = new Intent(getApplicationContext(), HomeActivity.class);
                    i.putExtra("ChapterId",myChapterId);
                    i.putExtra("ChapterName", myChapterName);
                    startActivity(i);
                }
            }, 2500);
        }
    }
}
