package org.onebrick.android.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import org.onebrick.android.OneBrickApplication;
import org.onebrick.android.OneBrickClient2;
import org.onebrick.android.R;
import org.onebrick.android.helpers.FontsHelper;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class SplashScreenActivity extends Activity {
    @InjectView(R.id.ivSplashScreenFooter) ImageView ivFooter;
    @InjectView(R.id.tvWelcomeNote) TextView tvWelcomeNote;
    @InjectView(R.id.ivLauncherLogo) ImageView ivObLogo;

    private static final String imageUri = "assets://volunteer_hands.png";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*
        Since this being splash screen requesting window with no title
         */
        // Remove title bar
        //this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //Remove notification bar
        /*this.getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);*/
        setContentView(R.layout.activity_splash_screen);
        // inject annotation
        ButterKnife.inject(this);

       // ivObLogo.setAlpha(0.0f);
       // ivObLogo.animate().alpha(1.0f).setDuration(3000);

        OneBrickClient2.getChapters();

        tvWelcomeNote.setTypeface(FontsHelper.getRobotoRegular());
        ImageLoader imgLoader =  ImageLoader.getInstance();
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
                    overridePendingTransition(R.anim.right_in, R.anim.left_out);
                }
            }, 3000);
        } else {
           new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent i = new Intent(getApplicationContext(), HomeActivity.class);
                    i.putExtra("ChapterId",myChapterId);
                    i.putExtra("ChapterName", myChapterName);
                    startActivity(i);
                    overridePendingTransition(R.anim.right_in, R.anim.left_out);
                }
            }, 3000);
        }
    }
}
