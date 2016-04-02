package org.onebrick.android.activities;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.activeandroid.content.ContentProvider;
import com.squareup.otto.Subscribe;

import org.onebrick.android.R;
import org.onebrick.android.adapters.CardArrayAdapter;
import org.onebrick.android.cards.ContactsCard;
import org.onebrick.android.cards.DescriptionCard;
import org.onebrick.android.cards.MapCard;
import org.onebrick.android.cards.PhotoGalleryCard;
import org.onebrick.android.cards.TitleCard;
import org.onebrick.android.core.OneBrickApplication;
import org.onebrick.android.core.OneBrickRESTClient;
import org.onebrick.android.events.FetchEventDetailEvent;
import org.onebrick.android.events.LoginStatusEvent;
import org.onebrick.android.events.Status;
import org.onebrick.android.helpers.DateTimeFormatter;
import org.onebrick.android.helpers.LoginManager;
import org.onebrick.android.helpers.Utils;
import org.onebrick.android.models.Event;
import org.onebrick.android.models.RSVP;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class EventDetailActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = EventDetailActivity.class.getSimpleName();
    public static final String EXTRA_EVENT_ID = "event_id";
    public static final String SUCCESS = "0";
    private static final String SOCIAL_URL_PREFIX = "http://onebrick.org/event/?eventid=";
    private static final int EVENT_DETAIL_LOADER = 77;

    @Bind(R.id.lv_event_detail_cards)
    ListView mCardsListView;
    @Bind(R.id.btn_rsvp)
    Button mBtnRsvp;
    @Bind(R.id.tvStatusMessage)
    TextView mTvStatusMessage;
    @Bind(R.id.ll_rsvp_segment)
    LinearLayout mLlRsvpSegment;

    private CardArrayAdapter mAdapter;
    private long mEventId;
    private Event mEvent;
    private boolean mPendingRsvp;
    private TitleCard mTitleCard;
    private MapCard mMapCard;
    private PhotoGalleryCard mPhotoGalleryCard;
    
    private void updateViews() {
        // TODO check current date for past events. if past events, don't show rsvp/unrsvp buttons
        if (DateTimeFormatter.getInstance().isPastEvent(mEvent.getEndDate()) || Utils.isEventCancelled(mEvent.getEventStatus())) {
            mLlRsvpSegment.setVisibility(View.GONE);
        } else if (LoginManager.getInstance(this).isLoggedIn() && mEvent.isRsvp()) {
            mBtnRsvp.setText(R.string.un_rsvp);
            mBtnRsvp.setBackgroundResource(R.drawable.btn_unrsvp_small);
        } else if (!DateTimeFormatter.getInstance().isRSVPOpen(mEvent.getRSVPOpenDate())){
            // event will open later
            mBtnRsvp.setVisibility(View.GONE);
            mTvStatusMessage.setVisibility(View.VISIBLE);
            mTvStatusMessage.setText(getResources().getText(R.string.rsvp_not_open) + DateTimeFormatter.getInstance().getFormattedEventDateOnly(mEvent.getRSVPOpenDate()));
        } else if (mEvent.getRsvpCount() >= mEvent.getRsvpCapacity()) {
            // event is full
            mBtnRsvp.setVisibility(View.GONE);
            mTvStatusMessage.setVisibility(View.VISIBLE);
            mTvStatusMessage.setText(getResources().getText(R.string.event_full));
        } else {
            mBtnRsvp.setText(R.string.rsvp);
            mBtnRsvp.setBackgroundResource(R.drawable.btn_rsvp_small);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.mi_item_share:
                setShareIntent();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        // call onLoaderReset
        getLoaderManager().destroyLoader(EVENT_DETAIL_LOADER);
        finish();
        overridePendingTransition(R.anim.left_in, R.anim.right_out);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate menu resource file.
        getMenuInflater().inflate(R.menu.event_detail, menu);
//        // Return true to display menu
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_info);
        getSupportActionBar().setTitle("Event Details");
        ButterKnife.bind(this);

        mAdapter = new CardArrayAdapter(this);
        mAdapter.setNotifyOnChange(false);
        mCardsListView.setAdapter(mAdapter);

        Intent eventInfo = getIntent();
        mEventId = eventInfo.getLongExtra(EXTRA_EVENT_ID, -1);

        if (savedInstanceState == null) {
            OneBrickRESTClient.getInstance().requestEventDetail(mEventId);
        }
        getSupportLoaderManager().initLoader(EVENT_DETAIL_LOADER, null, this);
        OneBrickApplication.getInstance().getBus().register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        OneBrickApplication.getInstance().getBus().unregister(this);
    }

    private void setupListeners() {
        mBtnRsvp.setOnClickListener(new View.OnClickListener() {
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
            if (mBtnRsvp.getText().toString().equalsIgnoreCase(getString(R.string.rsvp))) {
                processRSVP(ukey, eventId);
            } else if (mBtnRsvp.getText().toString().equalsIgnoreCase(getString(R.string.un_rsvp))) {
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
                    mBtnRsvp.setText(R.string.un_rsvp);
                    mBtnRsvp.setBackgroundResource(R.drawable.btn_unrsvp_small);
                    mEvent.rsvp();
                    mEvent.save();
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
                    mBtnRsvp.setText(R.string.rsvp);
                    mBtnRsvp.setBackgroundResource(R.drawable.btn_rsvp_small);
                    mEvent.unRsvp();
                    mEvent.save();
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

    // Call to update the share intent
    private void setShareIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        if (mEvent != null) {
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, mEvent.getTitle());
            shareIntent.putExtra(Intent.EXTRA_TEXT, mEvent.getTitle() + ":  " + SOCIAL_URL_PREFIX + mEvent.getEventId());
        }
        startActivity(Intent.createChooser(shareIntent, getString(R.string.send_intent_title)));
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
        String selection = Event.EVENT_ID + "=?";
        String[] selectionArgs = new String[]{Long.toString(mEventId)};

        return new CursorLoader(this,
                ContentProvider.createUri(Event.class, null),
                null,
                selection,
                selectionArgs,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        if (cursor != null && cursor.moveToFirst()) {
            mEvent = Event.fromCursor(cursor);
            mAdapter.clear();
            if (mTitleCard == null){
                mTitleCard = new TitleCard(this, mEvent);
            }
            mAdapter.add(mTitleCard);
            if (mPhotoGalleryCard == null){
                mPhotoGalleryCard = new PhotoGalleryCard(this, mEvent);
            }
            mAdapter.add(mPhotoGalleryCard);
            mAdapter.add(new DescriptionCard(this, mEvent));
            if (mMapCard == null){
                mMapCard = new MapCard(this, mEvent);
            }
            mAdapter.add(mMapCard);
            mAdapter.add(new ContactsCard(this, mEvent));
            mAdapter.notifyDataSetChanged();

            updateViews();
            setupListeners();
        } else {
            // TODO error
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mAdapter.clear();
        mEvent = null;
    }

    @Subscribe
    public void onLoginStatusEvent(LoginStatusEvent event) {
        if (mPendingRsvp && event.status == Status.SUCCESS) {
            processRsvpRequest(mEvent.getEventId());
        }
    }

    @Subscribe
    public void onFetchEventDetailEvent(FetchEventDetailEvent event) {
        if (event.status == Status.NO_NETWORK) {
            Toast.makeText(this, R.string.no_network, Toast.LENGTH_LONG).show();
        } else if (event.status == Status.FAILED) {
            Toast.makeText(this, R.string.failed_to_fetch_event_detail, Toast.LENGTH_LONG).show();
        }
    }
}