package org.onebrick.android.activities;

import android.content.Intent;
import android.os.Bundle;
import android.provider.CalendarContract;
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

public class EventInfoActivity extends ActionBarActivity {

    private static final String TAG = EventInfoActivity.class.getName().toString();

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

    Event updatedEvent;
    String eventId;
    OneBrickClient obClient;

    Event selectedEvent;
    LoginManager loginMgr;
    User user;
    DateTimeFormatter obDtf;

    /*
    This is the response handler to handle the callbacks from Event info rest call
     */
    JsonHttpResponseHandler responseHandler = new JsonHttpResponseHandler() {
        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            super.onSuccess(statusCode, headers, response);
            Log.d("TAG", "Success" + response.toString());
            updatedEvent = Event.getUpdatedEvent(response);
            updateViews(updatedEvent);
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
            Log.e("TAG", "Json Request to fetch event info failed");
            super.onFailure(statusCode, headers, throwable, errorResponse);
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
            Log.e("TAG", "FAIL " + responseString);
            super.onFailure(statusCode, headers, responseString, throwable);
        }

        @Override
        public void onStart() {
            super.onStart();
            llRsvpSegment.setVisibility(View.INVISIBLE);
            svMainContent.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        public void onFinish() {
            super.onFinish();
        }
    };

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
            updatedEvent.rsvp = true;
            Event.updateEvent(updatedEvent);

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
            updatedEvent.rsvp = false;
            Event.updateEvent(updatedEvent);
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
        String eventDesc = Utils.removeImgTagsFromHTML(updatedEvent.getEventDescription());
        eventDesc = Utils.removeHTagsFromHTML(eventDesc);
        tvEventBrief.setText(Html.fromHtml(eventDesc));
        tvEventLocation.setText(updatedEvent.getEventAddress());
        if(loginMgr.isLoggedIn()) {
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

        loginMgr = LoginManager.getInstance(getApplicationContext());
        obClient = OneBrickApplication.getInstance().getRestClient();
        Intent eventInfo = getIntent();
        eventId = eventInfo.getStringExtra("EventId");
        if(loginMgr.isLoggedIn()) {
            User usr = loginMgr.getCurrentUser();
            obClient.getEventInfo(eventId, usr.getUserId(), responseHandler);
        } else {
            obClient.getEventInfo(eventId, -1, responseHandler);
        }
        obDtf = DateTimeFormatter.getInstance();
        setupListeners();
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
                if (!loginMgr.isLoggedIn()) {
                    Intent loginActivity = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(loginActivity);
                    if (updatedEvent.rsvp == true) {
                        btnRsvp.setText(R.string.un_rsvp_button);
                        btnRsvp.setBackgroundResource(R.drawable.btn_unrsvp_small);
                    } else {
                        btnRsvp.setText(R.string.rsvp_button);
                        btnRsvp.setBackgroundResource(R.drawable.btn_rsvp_small);
                    }
                } else {
                    user = loginMgr.getCurrentUser();
                    if (btnRsvp.getText().toString().equalsIgnoreCase(getString(R.string.rsvp_button))) {
                        obClient.postRsvpToEvent(selectedEvent.eventId, user.getUserId(), rsvpResponseHandler);

                    } else if (btnRsvp.getText().toString().equalsIgnoreCase(getString(R.string.un_rsvp_button))) {
                        obClient.postUnRsvpToEvent(selectedEvent.eventId, user.getUserId(), unRsvpResponseHandler);
                    }
                }
            }
        });

        // email to manager
        btnEmailManager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = updatedEvent.getManagerEmail();
                if (Utils.isValidEmail(email)) {
                    SocialShareEmail.sendEmails(v, updatedEvent.getTitle(), updatedEvent.getEventId(), email);
                }
            }
        });

        // email to coordinator
        btnEmailCoordinator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = updatedEvent.getCoordinatorEmail();
                if (Utils.isValidEmail(email)) {
                    SocialShareEmail.sendEmails(v, updatedEvent.getTitle(), updatedEvent.getEventId(), email);
                }
            }
        });

        ivAdd2Calendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar beginTime = Calendar.getInstance();
                beginTime.setTime(obDtf.getDateFromString(updatedEvent.getEventStartDate()));
                Calendar endTime = Calendar.getInstance();
                beginTime.setTime(obDtf.getDateFromString(updatedEvent.getEventEndDate()));
                Intent intent = new Intent(Intent.ACTION_INSERT)
                        .setData(CalendarContract.Events.CONTENT_URI)
                        .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, beginTime.getTimeInMillis())
                        .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTime.getTimeInMillis())
                        .putExtra(CalendarContract.Events.TITLE, updatedEvent.getTitle())
                        .putExtra(CalendarContract.Events.EVENT_LOCATION, updatedEvent.getEventAddress())
                        .putExtra(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_BUSY);
                startActivity(intent);
                ivAdd2Calendar.setImageResource(R.drawable.ic_in_calendar);
            }
        });

        ivEventInfoFbShare.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                SocialShareEmail.shareFacebook(v, updatedEvent.getTitle(), updatedEvent.eventId);
            }
        });

        ivEventInfoTwitterShare.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                SocialShareEmail.shareTwitter(v, updatedEvent.getTitle(), updatedEvent.getEventId());
            }
        });

        ivEventInfoGenShare.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                SocialShareEmail.shareOthers(v, updatedEvent.getTitle(), updatedEvent.getEventId());
            }
        });
    }
}
