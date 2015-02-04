package org.onebrick.android.activities;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.activeandroid.content.ContentProvider;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.apache.http.Header;
import org.json.JSONObject;
import org.onebrick.android.R;
import org.onebrick.android.core.OneBrickApplication;
import org.onebrick.android.core.OneBrickClient;
import org.onebrick.android.helpers.DateTimeFormatter;
import org.onebrick.android.helpers.LoginManager;
import org.onebrick.android.helpers.SocialShareEmail;
import org.onebrick.android.helpers.Utils;
import org.onebrick.android.models.Event;
import org.onebrick.android.models.User;

import java.util.Calendar;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class EventInfoActivity extends ActionBarActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = EventInfoActivity.class.getName();

    public static final String EXTRA_EVENT_ID = "event_id";

    @InjectView(R.id.ivProfilePhoto) ImageView ivProfilePhoto;
    @InjectView(R.id.rlContact) RelativeLayout rlContact;
    @InjectView(R.id.tvEventName) TextView tvEventName;
    @InjectView(R.id.tvEventTime) TextView tvEventDateTime;
    @InjectView(R.id.tvEventBrief) TextView tvEventBrief;
    @InjectView(R.id.tvEventLocation) TextView tvEventLocation;
    @InjectView(R.id.tvLearnMore) TextView tvLearnMore;
    @InjectView(R.id.btnRsvp) Button btnRsvp;
    @InjectView(R.id.btnEmailManager) Button btnEmailManager;
    @InjectView(R.id.btnEmailCoordinator) Button btnEmailCoordinator;
    @InjectView(R.id.ivCalendarIcon) ImageView ivAdd2Calendar;
    @InjectView(R.id.ivEventInfoFbShare) ImageView ivEventInfoFbShare;
    @InjectView(R.id.ivEventInfoTwitterShare) ImageView ivEventInfoTwitterShare;
    @InjectView(R.id.ivEventInfoGenShare) ImageView ivEventInfoGenShare;
    @InjectView(R.id.pbEventInfoProgress) ProgressBar progressBar;
    @InjectView(R.id.svMainContent) ScrollView svMainContent;
    @InjectView(R.id.rlRsvp) LinearLayout llRsvpSegment;
    @InjectView(R.id.llDummySpace) View llDummySpace;

    private long eventId;
    Event mEvent;
    OneBrickClient obClient;

    Event selectedEvent;
    DateTimeFormatter obDtf;


    // TODO make REST calls in service
    /*
    This is the response handler to handle the callback from RSVP rest request
     */
    JsonHttpResponseHandler rsvpResponseHandler = new JsonHttpResponseHandler() {
        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            //Toast.makeText(getApplication(),"RSVP Success",Toast.LENGTH_SHORT).show();
            btnRsvp.setText(R.string.un_rsvp_button);
            btnRsvp.setBackgroundResource(R.drawable.btn_unrsvp_small);
            mEvent.rsvp = true;
            Event.updateEvent(mEvent);

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
            //Toast.makeText(getApplication(),"UnRSVP Success",Toast.LENGTH_SHORT).show();
            btnRsvp.setText(R.string.rsvp_button);
            btnRsvp.setBackgroundResource(R.drawable.btn_rsvp_small);
            mEvent.rsvp = false;
            Event.updateEvent(mEvent);
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

    private void updateViews(Event updatedEvent) {

        Log.d(TAG,"Selected Event is "+updatedEvent);
        selectedEvent = updatedEvent;

        final ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.displayImage(updatedEvent.getProfilePhotoUri(), ivProfilePhoto);

        tvEventName.setText(updatedEvent.getTitle());
        tvEventDateTime.setText(DateTimeFormatter.getInstance().getFormattedEventDate(updatedEvent.getEventStartDate())
                + " - "
                + DateTimeFormatter.getInstance().getFormattedTimeEndOnly(updatedEvent.getEventStartDate(), updatedEvent.getEventEndDate()));
        if (updatedEvent.getEventDescription() != null) {
            String eventDesc = Utils.removeImgTagsFromHTML(updatedEvent.getEventDescription());
            eventDesc = Utils.removeHTagsFromHTML(eventDesc);
            tvEventBrief.setText(Html.fromHtml(eventDesc));
        }
        tvEventLocation.setText(updatedEvent.getEventAddress());
        if(LoginManager.getInstance(this).isLoggedIn()) {
            if (updatedEvent.rsvp == true) {
                btnRsvp.setText(R.string.un_rsvp_button);
                btnRsvp.setBackgroundResource(R.drawable.btn_unrsvp_small);
            } else {
                btnRsvp.setText(R.string.rsvp_button);
                btnRsvp.setBackgroundResource(R.drawable.btn_rsvp_small);
            }
        }
        progressBar.setVisibility(View.INVISIBLE);
        svMainContent.setVisibility(View.VISIBLE);

        // check current date for past events. if past events, don't show rsvp/unrsvp buttons
        if (DateTimeFormatter.getInstance().isPastEvent(updatedEvent.getEventEndDate())){
            llRsvpSegment.setVisibility(View.GONE);
            llDummySpace.setVisibility(View.GONE);
        }else{
            llRsvpSegment.setVisibility(View.VISIBLE);
        }

        // set up email contacts visibility
        if (!Utils.isValidEmail(updatedEvent.getManagerEmail()) && !Utils.isValidEmail(updatedEvent.getCoordinatorEmail())) {
            rlContact.setVisibility(View.GONE);
        }else{
            if (!Utils.isValidEmail(updatedEvent.getManagerEmail())){
                btnEmailManager.setVisibility(View.GONE);
            }else if (!Utils.isValidEmail(updatedEvent.getCoordinatorEmail())){
                btnEmailCoordinator.setVisibility(View.GONE);
            }
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
        progressBar.setVisibility(View.INVISIBLE);

        obClient = OneBrickApplication.getInstance().getRestClient();
        Intent eventInfo = getIntent();
        eventId = eventInfo.getLongExtra(EXTRA_EVENT_ID, -1);
        obDtf = DateTimeFormatter.getInstance();
        setupListeners();

        getSupportLoaderManager().initLoader(0, null, this);
    }

    private void setupListeners() {
         tvEventBrief.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                        Intent eventDetails = new Intent(getApplicationContext(), EventDescriptionActivity.class);
                eventDetails.putExtra("Details", "" + selectedEvent.getEventDescription());
                startActivity(eventDetails);
                        overridePendingTransition(R.anim.right_in, R.anim.left_out);
            }
        });

        tvLearnMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent eventDetails = new Intent(getApplicationContext(), EventDescriptionActivity.class);
                eventDetails.putExtra("Details", "" + selectedEvent.getEventDescription());
                startActivity(eventDetails);
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
            }
        });

        btnRsvp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final LoginManager loginManager = LoginManager.getInstance(EventInfoActivity.this);
                if (!loginManager.isLoggedIn()) {
                    Intent loginActivity = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(loginActivity);
                    if (mEvent.rsvp == true) {
                        btnRsvp.setText(R.string.un_rsvp_button);
                        btnRsvp.setBackgroundResource(R.drawable.btn_unrsvp_small);
                    } else {
                        btnRsvp.setText(R.string.rsvp_button);
                        btnRsvp.setBackgroundResource(R.drawable.btn_rsvp_small);
                    }
                } else {
                    final User currentUser = loginManager.getCurrentUser();
                    if (btnRsvp.getText().toString().equalsIgnoreCase(getString(R.string.rsvp_button))) {
                        obClient.postRsvpToEvent(selectedEvent.eventId, currentUser.getUserId(), rsvpResponseHandler);

                    } else if (btnRsvp.getText().toString().equalsIgnoreCase(getString(R.string.un_rsvp_button))) {
                        obClient.postUnRsvpToEvent(selectedEvent.eventId, currentUser.getUserId(), unRsvpResponseHandler);
                    }
                }
            }
        });

        // email to manager
        btnEmailManager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mEvent.getManagerEmail();
                if (Utils.isValidEmail(email)) {
                    SocialShareEmail.sendEmails(v, mEvent.getTitle(), mEvent.getEventId(), email);
                }
            }
        });

        // email to coordinator
        btnEmailCoordinator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mEvent.getCoordinatorEmail();
                if (Utils.isValidEmail(email)) {
                    SocialShareEmail.sendEmails(v, mEvent.getTitle(), mEvent.getEventId(), email);
                }
            }
        });

        ivAdd2Calendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar beginTime = Calendar.getInstance();
                beginTime.setTime(obDtf.getDateFromString(mEvent.getEventStartDate()));
                Calendar endTime = Calendar.getInstance();
                beginTime.setTime(obDtf.getDateFromString(mEvent.getEventEndDate()));
                Intent intent = new Intent(Intent.ACTION_INSERT)
                        .setData(CalendarContract.Events.CONTENT_URI)
                        .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, beginTime.getTimeInMillis())
                        .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTime.getTimeInMillis())
                        .putExtra(CalendarContract.Events.TITLE, mEvent.getTitle())
                        .putExtra(CalendarContract.Events.EVENT_LOCATION, mEvent.getEventAddress())
                        .putExtra(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_BUSY);
                startActivity(intent);
                ivAdd2Calendar.setImageResource(R.drawable.ic_in_calendar);
            }
        });

        ivEventInfoFbShare.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                SocialShareEmail.shareFacebook(v, mEvent.getTitle(), mEvent.eventId);
            }
        });

        ivEventInfoTwitterShare.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                SocialShareEmail.shareTwitter(v, mEvent.getTitle(), mEvent.getEventId());
            }
        });

        ivEventInfoGenShare.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                SocialShareEmail.shareOthers(v, mEvent.getTitle(), mEvent.getEventId());
            }
        });
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
        return new CursorLoader(this, ContentProvider.createUri(Event.class, eventId),
                null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        if (cursor != null && cursor.moveToFirst()) {
            mEvent = Event.fromCursor(cursor);
            updateViews(mEvent);
        } else {
            // TODO error
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
    }
}
