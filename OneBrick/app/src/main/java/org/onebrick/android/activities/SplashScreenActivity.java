package org.onebrick.android.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.apache.http.Header;
import org.json.JSONObject;
import org.onebrick.android.core.OneBrickApplication;
import org.onebrick.android.R;
import org.onebrick.android.helpers.FontsHelper;
import org.onebrick.android.models.Chapter;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class SplashScreenActivity extends ActionBarActivity {
    private static final String TAG = "SplashScreenActivity";

    @InjectView(R.id.ivSplashScreenFooter) ImageView ivFooter;
    @InjectView(R.id.tvWelcomeNote) TextView tvWelcomeNote;

    private static final String imageUri = "assets://volunteer_hands.png";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        // inject annotation
        ButterKnife.inject(this);

        tvWelcomeNote.setTypeface(FontsHelper.getRobotoRegular());
        final ImageLoader imgLoader =  ImageLoader.getInstance();
        imgLoader.displayImage(imageUri, ivFooter);

        final String  myChapterName = OneBrickApplication.getInstance().getChapterName();
        final int myChapterId = OneBrickApplication.getInstance().getChapterId();

        if(myChapterName == null) {
            // TODO move this to Service to make REST calls
            OneBrickApplication.getInstance().getRestClient()
                    .getChapters(new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    Chapter.getChapterListFromJsonObject(response);
                    Log.d(TAG, "get chapters api call success");
                }

                @Override
                public void onFailure(int statusCode, Header[] headers,
                                      Throwable throwable, JSONObject errorResponse) {
                    Log.w(TAG, "get chapters api call failed");
                }
            });

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent i = new Intent(SplashScreenActivity.this, SelectChapterActivity.class);
                    startActivity(i);
                    overridePendingTransition(R.anim.right_in, R.anim.left_out);
                }
            }, 2000);
        } else {
           new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent i = new Intent(getApplicationContext(), HomeActivity.class);
                    i.putExtra(HomeActivity.EXTRA_CHAPTER_ID, myChapterId);
                    i.putExtra(HomeActivity.EXTRA_CHAPTER_NAME, myChapterName);
                    startActivity(i);
                    overridePendingTransition(R.anim.right_in, R.anim.left_out);
                }
            }, 2000);
        }
    }
}
