package org.onebrick.android.activities;

import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.squareup.otto.Subscribe;

import org.onebrick.android.R;
import org.onebrick.android.adapters.CardArrayAdapter;
import org.onebrick.android.cards.ContactsCard;
import org.onebrick.android.cards.DescriptionCard;
import org.onebrick.android.cards.MapCard;
import org.onebrick.android.cards.PhotoGalleryCard;
import org.onebrick.android.cards.ShareCard;
import org.onebrick.android.cards.TitleCard;
import org.onebrick.android.core.OneBrickApplication;
import org.onebrick.android.core.OneBrickRESTClient;
import org.onebrick.android.events.LoginStatusEvent;
import org.onebrick.android.events.Status;
import org.onebrick.android.helpers.DateTimeFormatter;
import org.onebrick.android.helpers.LoginManager;
import org.onebrick.android.models.Event;
import org.onebrick.android.models.RSVP;
import org.onebrick.android.providers.OneBrickContentProvider;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * TODO this class should be revisited
 */
public class EventDetailActivity extends ActionBarActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = "EventDetailActivity";
    public static final String EXTRA__ID = "_id";
    public static final String SUCCESS = "0";

    @InjectView(R.id.btn_rsvp)
    Button btnRsvp;
    @InjectView(R.id.lv_event_detail_cards)
    ListView mCardsListView;

    private CardArrayAdapter mAdapter;
    private long _Id;
    private Event mEvent;
    private boolean mPendingRsvp;

    private void updateViews() {
        if (LoginManager.getInstance(this).isLoggedIn()) {
            if (mEvent.isRsvp()) {
                btnRsvp.setText(R.string.un_rsvp);
                btnRsvp.setBackgroundResource(R.drawable.btn_unrsvp_small);
            } else {
                btnRsvp.setText(R.string.rsvp);
                btnRsvp.setBackgroundResource(R.drawable.btn_rsvp_small);
            }
        }

        // TODO check current date for past events. if past events, don't show rsvp/unrsvp buttons
        if (DateTimeFormatter.getInstance().isPastEvent(mEvent.getEndDate())) {
            //llRsvpSegment.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.left_in, R.anim.right_out);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_info);
        getSupportActionBar().setTitle("Event Details");
        ButterKnife.inject(this);

        mAdapter = new CardArrayAdapter(this);
        mAdapter.setNotifyOnChange(false);
        mCardsListView.setAdapter(mAdapter);

        Intent eventInfo = getIntent();
        _Id = eventInfo.getLongExtra(EXTRA__ID, -1);
        getSupportLoaderManager().initLoader(0, null, this);

        OneBrickApplication.getInstance().getBus().register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        OneBrickApplication.getInstance().getBus().unregister(this);
    }


    private void setupListeners() {
        btnRsvp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final LoginManager loginManager = LoginManager.getInstance(EventDetailActivity.this);

                final long eventId = mEvent.getEventId();
                if (!loginManager.isLoggedIn()) {
                    mPendingRsvp = true;

                    // when a user is not logged in yet, redirect a user to login screen
                    final Intent loginActivity = new Intent(EventDetailActivity.this, LoginActivity.class);
                    startActivity(loginActivity);
                } else {
                    // already logged in. just get stored ukey
                    processRsvpRequest(eventId);
                }
            }
        });
    }

    private void processRsvpRequest(long eventId) {
        final LoginManager loginManager = LoginManager.getInstance(this);
        final String ukey = loginManager.getCurrentUserKey();
        if (!TextUtils.isEmpty(ukey)) {
            if (btnRsvp.getText().toString().equalsIgnoreCase(getString(R.string.rsvp))) {
                processRSVP(ukey, eventId);
            } else if (btnRsvp.getText().toString().equalsIgnoreCase(getString(R.string.un_rsvp))) {
                processUnRSVP(ukey, eventId);
            }
        }
    }

    /**
     * This is the response handler to handle the callback from RSVP rest request
     *
     * @param ukey
     * @param eventId
     */
    private void processRSVP(String ukey, long eventId) {
        OneBrickRESTClient.getInstance().rsvp(ukey, eventId, new Callback<RSVP>() {

            @Override
            public void success(RSVP result, Response response) {
                if (result != null && SUCCESS.equals(result.getCode())) {
                    Log.d(TAG, "rsvp result: " + result.getCode() + "---" + result.getMessage());
                    btnRsvp.setText(R.string.un_rsvp);
                    btnRsvp.setBackgroundResource(R.drawable.btn_unrsvp_small);
                    mEvent.rsvp();
                    // TODO update through SyncAdapter
                    //Event.updateEvent(mEvent);
                } else {
                    Log.d(TAG, "rsvp result return null: ");
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e(TAG, "failed rsvp");
            }
        });
    }

    /**
     * This is the response handle to handle the callbacks from unRSVP rest request
     *
     * @param ukey
     * @param eventId
     */
    private void processUnRSVP(String ukey, long eventId) {
        //obClient.postUnRsvpToEvent(mEvent.getEventId(), key, unRsvpResponseHandler);
        OneBrickRESTClient.getInstance().unrsvp(ukey, eventId, new Callback<RSVP>() {
            @Override
            public void success(RSVP result, Response response) {
                if (result != null && SUCCESS.equals(result.getCode())) {
                    Log.d(TAG, "unrsvp result: " + result.getCode() + "---" + result.getMessage());
                    btnRsvp.setText(R.string.rsvp);
                    btnRsvp.setBackgroundResource(R.drawable.btn_rsvp_small);
                    mEvent.unRsvp();
                    // TODO update through SyncAdapter
                    //Event.updateEvent(mEvent);
                } else {
                    Log.d(TAG, "unrsvp result return null: ");
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e(TAG, "failed to unrsvp");
            }
        });
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
        final Uri uri = ContentUris.withAppendedId(OneBrickContentProvider.EVENTS_URI, _Id);
        return new CursorLoader(this, uri,
                null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        if (cursor != null && cursor.moveToFirst()) {
            mEvent = Event.fromCursor(cursor);

            mAdapter.clear();
            mAdapter.add(new TitleCard(this, mEvent));
            mAdapter.add(new PhotoGalleryCard(this, mEvent));
            mAdapter.add(new DescriptionCard(this, mEvent));
            mAdapter.add(new MapCard(this, mEvent));
            mAdapter.add(new ContactsCard(this, mEvent));
            mAdapter.add(new ShareCard(this, mEvent));
            mAdapter.notifyDataSetChanged();

            updateViews();
            setupListeners();

        } else {
            // TODO error
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
    }


    @Subscribe
    public void onLoginStatusEvent(LoginStatusEvent event) {
        if (mPendingRsvp && event.status == Status.SUCCESS) {
            processRsvpRequest(mEvent.getEventId());
        }
    }
}


