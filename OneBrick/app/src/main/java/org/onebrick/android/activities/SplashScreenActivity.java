package org.onebrick.android.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import org.onebrick.android.R;
import org.onebrick.android.core.OneBrickApplication;
import org.onebrick.android.core.OneBrickRESTClient;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SplashScreenActivity extends AppCompatActivity {
    private static final String TAG = "SplashScreenActivity";

    private static final String imageUri = "assets://volunteer_hands.png";

    @Bind(R.id.ivSplashScreenFooter) ImageView ivFooter;
    @Bind(R.id.tvWelcomeNote) TextView tvWelcomeNote;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        ButterKnife.bind(this);

        final ImageLoader imgLoader =  ImageLoader.getInstance();
        imgLoader.displayImage(imageUri, ivFooter);

        final String  myChapterName = OneBrickApplication.getInstance().getChapterName();
        final int myChapterId = OneBrickApplication.getInstance().getChapterId();

        if(myChapterName == null) {
            OneBrickRESTClient.getInstance().requestChapters();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    final Intent intent = new Intent(SplashScreenActivity.this, SelectChapterActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.right_in, R.anim.left_out);
                }
            }, 2000);
        } else {
           new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    final Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                    intent.putExtra(HomeActivity.EXTRA_CHAPTER_ID, myChapterId);
                    intent.putExtra(HomeActivity.EXTRA_CHAPTER_NAME, myChapterName);
                    startActivity(intent);
                    overridePendingTransition(R.anim.right_in, R.anim.left_out);
                }
            }, 2000);
        }
    }
}
