package org.onebrick.android.activities;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
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
import org.onebrick.android.R;
import org.onebrick.android.core.OneBrickApplication;
import org.onebrick.android.database.ChapterTable;
import org.onebrick.android.helpers.FontsHelper;
import org.onebrick.android.models.Chapter;
import org.onebrick.android.providers.ChapterContentProvider;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class SplashScreenActivity extends ActionBarActivity {
    private static final String TAG = "SplashScreenActivity";

    private static final String imageUri = "assets://volunteer_hands.png";

    // The authority for the sync adapter's content provider
    // An account type, in the form of a domain name
    public static final String ACCOUNT_TYPE = "onebrick.org";
    // The account name
    public static final String ACCOUNT = "SyncAdapterAccount";

    private Account mAccount;

    @InjectView(R.id.ivSplashScreenFooter) ImageView ivFooter;
    @InjectView(R.id.tvWelcomeNote) TextView tvWelcomeNote;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        mAccount = createSyncAccount(this);

        ButterKnife.inject(this);

        tvWelcomeNote.setTypeface(FontsHelper.getRobotoRegular());
        final ImageLoader imgLoader =  ImageLoader.getInstance();
        imgLoader.displayImage(imageUri, ivFooter);

        final String  myChapterName = OneBrickApplication.getInstance().getChapterName();
        final int myChapterId = OneBrickApplication.getInstance().getChapterId();

        if(myChapterName == null) {
            //requestChapters();
            OneBrickApplication.getInstance().getRestClient()
                    .getChapters(new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            Log.d(TAG, "get chapters api call success");
                            // TODO should be inserted through SyncAdapter
                            List<Chapter> chapters = Chapter.listFromJson(response);
//                    final List<ContentValues> valuesList = new ArrayList<>(chapters.size());
                            for (Chapter chapter : chapters) {
                                final ContentValues values = new ContentValues(2);
                                values.put(ChapterTable.Columns.CHAPTER_ID, chapter.getChapterId());
                                values.put(ChapterTable.Columns.NAME, chapter.getChapterName());
//                        valuesList.add(values);
                                getContentResolver().insert(ChapterContentProvider.CONTENT_URI, values);
                            }
//                    getContentResolver().bulkInsert(ChapterContentProvider.CONTENT_URI,
//                            valuesList.toArray(new ContentValues[0]));
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

    /**
     * Create a new dummy account for the sync adapter
     *
     * @param context The application context
     */
    private static Account createSyncAccount(Context context) {
        // Create the account type and default account
        Account newAccount = new Account(ACCOUNT, ACCOUNT_TYPE);
        AccountManager accountManager = (AccountManager) context.getSystemService(ACCOUNT_SERVICE);
        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
        accountManager.addAccountExplicitly(newAccount, null, null);
        return newAccount;
    }

    private void requestChapters() {
        Bundle settingsBundle = new Bundle();
        settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        ContentResolver.requestSync(mAccount, ChapterContentProvider.AUTHORITY, settingsBundle);
    }
}
