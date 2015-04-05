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
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONObject;
import org.onebrick.android.R;
import org.onebrick.android.adapters.CardArrayAdapter;
import org.onebrick.android.cards.ContactsCard;
import org.onebrick.android.cards.DescriptionCard;
import org.onebrick.android.cards.MapCard;
import org.onebrick.android.cards.PhotoGalleryCard;
import org.onebrick.android.cards.ShareCard;
import org.onebrick.android.cards.TitleCard;
import org.onebrick.android.core.OneBrickApplication;
import org.onebrick.android.core.OneBrickClient;
import org.onebrick.android.helpers.DateTimeFormatter;
import org.onebrick.android.helpers.LoginManager;
import org.onebrick.android.models.Event;
import org.onebrick.android.models.User;
import org.onebrick.android.providers.OneBrickContentProvider;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 TODO this class should be revisited
 */
public class EventDetailActivity extends ActionBarActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = "EventDetailActivity";
    public static final String EXTRA__ID = "_id";

    @InjectView(R.id.btn_rsvp) Button btnRsvp;
    @InjectView(R.id.lv_event_detail_cards) ListView mCardsListView;

    private CardArrayAdapter mAdapter;
    private long _Id;
    private Event mEvent;
    private OneBrickClient obClient;

    // TODO make REST calls in service
    /*
    This is the response handler to handle the callback from RSVP rest request
     */
    JsonHttpResponseHandler rsvpResponseHandler = new JsonHttpResponseHandler() {
        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            btnRsvp.setText(R.string.un_rsvp);
            btnRsvp.setBackgroundResource(R.drawable.btn_unrsvp_small);
            mEvent.rsvp();
            // TODO update through SyncAdapter
            //Event.updateEvent(mEvent);

        }

        @Override
        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
            Log.e("TAG", "Json Request to fetch event info failed");
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
            Log.e("TAG", "FAIL " + responseString);
        }

    };

    /*
    This is the response handle to handle the callbacks from unRSVP rest request
     */
    JsonHttpResponseHandler unRsvpResponseHandler = new JsonHttpResponseHandler() {
        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            btnRsvp.setText(R.string.rsvp);
            btnRsvp.setBackgroundResource(R.drawable.btn_rsvp_small);
            mEvent.unRsvp();
            // TODO update through SyncAdapter
            //Event.updateEvent(mEvent);
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
            Log.e("TAG", "Json Request to fetch event info failed");
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
            Log.e("TAG", "FAIL " + responseString);
        }

    };

    private void updateViews() {
        if(LoginManager.getInstance(this).isLoggedIn()) {
            if (mEvent.isRsvp()) {
                btnRsvp.setText(R.string.un_rsvp);
                btnRsvp.setBackgroundResource(R.drawable.btn_unrsvp_small);
            } else {
                btnRsvp.setText(R.string.rsvp);
                btnRsvp.setBackgroundResource(R.drawable.btn_rsvp_small);
            }
        }

        // TODO check current date for past events. if past events, don't show rsvp/unrsvp buttons
        if (DateTimeFormatter.getInstance().isPastEvent(mEvent.getEndDate())){
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

        obClient = OneBrickApplication.getInstance().getRestClient();
        Intent eventInfo = getIntent();
        _Id = eventInfo.getLongExtra(EXTRA__ID, -1);
        getSupportLoaderManager().initLoader(0, null, this);
    }

    private void setupListeners() {
        btnRsvp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final LoginManager loginManager = LoginManager.getInstance(EventDetailActivity.this);
                if (!loginManager.isLoggedIn()) {
                    final Intent loginActivity = new Intent(EventDetailActivity.this, LoginActivity.class);
                    startActivity(loginActivity);
                    if (mEvent.isRsvp()) {
                        btnRsvp.setText(R.string.un_rsvp);
                        btnRsvp.setBackgroundResource(R.drawable.btn_unrsvp_small);
                    } else {
                        btnRsvp.setText(R.string.rsvp);
                        btnRsvp.setBackgroundResource(R.drawable.btn_rsvp_small);
                    }
                } else {
                    final User currentUser = loginManager.getCurrentUser();
                    if (btnRsvp.getText().toString().equalsIgnoreCase(getString(R.string.rsvp))) {
                        obClient.postRsvpToEvent(mEvent.getEventId(), currentUser.getUserId(), rsvpResponseHandler);

                    } else if (btnRsvp.getText().toString().equalsIgnoreCase(getString(R.string.un_rsvp))) {
                        obClient.postUnRsvpToEvent(mEvent.getEventId(), currentUser.getUserId(), unRsvpResponseHandler);
                    }
                }
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



}


