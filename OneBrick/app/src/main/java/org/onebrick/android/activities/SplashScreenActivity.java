package org.onebrick.android.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;

import org.onebrick.android.OneBrickApplication;
import org.onebrick.android.R;

public class SplashScreenActivity extends Activity {
    ImageView ivFooter;
    ImageLoader imgLoader =  ImageLoader.getInstance();
    String imageUri = "assets://volunteer_hands.png";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*
        Since this being splash screen requesting window with no title
         */
        // Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //Remove notification bar
        this.getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash_screen);


        ivFooter = (ImageView) findViewById(R.id.ivSplashScreenFooter);
        imgLoader.displayImage(imageUri,ivFooter);
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
            }, 9500);
        } else {
           new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent i = new Intent(getApplicationContext(), HomeActivity.class);
                    i.putExtra("ChapterId",myChapterId);
                    i.putExtra("ChapterName", myChapterName);
                    startActivity(i);
                }
            }, 3000);
        }
    }
}
