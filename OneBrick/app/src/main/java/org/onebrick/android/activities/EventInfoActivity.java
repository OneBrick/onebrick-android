package org.onebrick.android.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.apache.http.Header;
import org.json.JSONObject;
import org.onebrick.android.OneBrickApplication;
import org.onebrick.android.OneBrickClient;
import org.onebrick.android.R;
import org.onebrick.android.helpers.DateTimeFormatter;
import org.onebrick.android.helpers.LoginManager;
import org.onebrick.android.helpers.OneBrickGeoCoder;
import org.onebrick.android.helpers.SocialShareEmail;
import org.onebrick.android.helpers.Utils;
import org.onebrick.android.models.Event;
import org.onebrick.android.models.User;

import java.util.Calendar;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class EventInfoActivity extends ActionBarActivity implements
        GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener {

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
    //ImageView ivAddReminder;
    @InjectView(R.id.ivEventInfoFbShare) ImageView ivEventInfoFbShare;
    @InjectView(R.id.ivEventInfoTwitterShare) ImageView ivEventInfoTwitterShare;
    @InjectView(R.id.ivEventInfoGenShare) ImageView ivEventInfoGenShare;
    @InjectView(R.id.pbEventInfoProgress) ProgressBar progressBar;
    @InjectView(R.id.svMainContent) ScrollView svMainContent;
    @InjectView(R.id.rlRsvp) LinearLayout llRsvpSegment;
    @InjectView(R.id.llDummySpace) View llDummySpace;

    Event updatedEvent;
    String eventId;
    private static final String TAG = HomeActivity.class.getName().toString();
    OneBrickClient obClient;
    private SupportMapFragment mapFragment;
    private GoogleMap map;
    private LocationClient mLocationClient;
    private MarkerOptions marker;
    Event selectedEvent;
    LoginManager loginMgr;
    User user;
    Geocoder obGeoCoder;
    DateTimeFormatter obDtf;
    double lat;
    double lng;

    /*
     * Define a request code to send to Google Play services This code is
     * returned in Activity.onActivityResult
     */
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

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

    /*
    This is the response handler to handle the callback from RSVP rest request
     */
    JsonHttpResponseHandler rsvpResponseHandler = new JsonHttpResponseHandler() {
        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            //super.onSuccess(statusCode, headers, response);
            //Toast.makeText(getApplication(),"RSVP Success",Toast.LENGTH_SHORT).show();
            btnRsvp.setText(R.string.un_rsvp_button);
            btnRsvp.setBackgroundResource(R.drawable.btn_unrsvp_small);
            updatedEvent.rsvp = true;
            Event.updateEvent(updatedEvent);

        }

        @Override
        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
            Log.e("TAG", "Json Request to fetch event info failed");
            //super.onFailure(statusCode, headers, throwable, errorResponse);
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
            Log.e("TAG", "FAIL " + responseString);
            //super.onFailure(statusCode, headers, responseString, throwable);
        }

    };

    /*
    This is the response handle to handle the callbacks from unRSVP rest request
     */
    JsonHttpResponseHandler unRsvpResponseHandler = new JsonHttpResponseHandler() {
        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            //super.onSuccess(statusCode, headers, response);
            //Toast.makeText(getApplication(),"UnRSVP Success",Toast.LENGTH_SHORT).show();
            btnRsvp.setText(R.string.rsvp_button);
            btnRsvp.setBackgroundResource(R.drawable.btn_rsvp_small);
            updatedEvent.rsvp = false;
            Event.updateEvent(updatedEvent);
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
            Log.e("TAG", "Json Request to fetch event info failed");
            //super.onFailure(statusCode, headers, throwable, errorResponse);
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
            Log.e("TAG", "FAIL " + responseString);
            //super.onFailure(statusCode, headers, responseString, throwable);
        }

    };

    private void updateViews(Event updatedEvent) {

        Log.d(TAG,"Selected Event is "+updatedEvent);
        selectedEvent = updatedEvent;

        final ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.displayImage(updatedEvent.getProfilePhotoUri(), ivProfilePhoto);

        tvEventName.setText(updatedEvent.getTitle());
        tvEventDateTime.setText(Utils.getFormattedEventDate(updatedEvent.getEventStartDate())
                + " - "
                + Utils.getFormattedTimeEndOnly(updatedEvent.getEventStartDate(), updatedEvent.getEventEndDate()));
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
        if (Utils.isPastEvent(updatedEvent.getEventEndDate())){
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

        //updateMapsFragment(updatedEvent);
        UpdateMapsFragment mapsUpdate = new UpdateMapsFragment();
        mapsUpdate.execute("Maps Update");

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
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
        // annotation injection
        ButterKnife.inject(this);
        // Setting the action bar title
//        ivAddReminder = (ImageView) findViewById(R.id.ivEventInfoAddReminder);
        progressBar.setVisibility(View.INVISIBLE);

        loginMgr = LoginManager.getInstance(getApplicationContext());
        obClient = OneBrickApplication.getRestClient();
        obGeoCoder = OneBrickGeoCoder.getInstance();
        Intent eventInfo = getIntent();
        eventId = eventInfo.getStringExtra("EventId");
        // Using GeoCoder so not using he api call
        if(loginMgr.isLoggedIn()) {
            User usr = loginMgr.getCurrentUser();
            obClient.getEventInfo(eventId, usr.getUId(), responseHandler);
        } else {
            obClient.getEventInfo(eventId, -1, responseHandler);
        }
        obDtf = DateTimeFormatter.getInstance();

        // Loading map
        mLocationClient = new LocationClient(this, this, this);
        mapFragment =
                ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapFrame));
        if (mapFragment != null) {
            map = mapFragment.getMap();
            if (map != null) {
            } else {
                Toast.makeText(this, "Error - Map was null!!", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, "Error - Map Fragment was null!!", Toast.LENGTH_LONG).show();
        }
        setupListeners();
    }

    private void setupListeners() {
        /*
        Setting up onClick listener on Learn more
        which when clicked the user will be taken to a new
        activity where he will get more detail description on the event.
         */
         tvEventBrief.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                        Intent eventDetails = new Intent(getApplicationContext(), EventDescription.class);
                eventDetails.putExtra("Details", "" + selectedEvent.getEventDescription());
                startActivity(eventDetails);
                        overridePendingTransition(R.anim.right_in, R.anim.left_out);
            }
        });

        tvLearnMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent eventDetails = new Intent(getApplicationContext(), EventDescription.class);
                eventDetails.putExtra("Details", "" + selectedEvent.getEventDescription());
                startActivity(eventDetails);
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
            }
        });

        /*
        Setting up onClick listener for RSVP button. When this button is clicked
        the current user is checked if he is already logged in or not.
        If the user is not logged in he is prompted to login via login activity.
        If the user is already loged in the async rsvp requet is send and on success the button
        is changed to un-rsvp
         */
        btnRsvp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!loginMgr.isLoggedIn()) {
                    /*
                    If the user is not logged in the prompting the use to login
                     */
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
                    //Toast.makeText(getApplicationContext(),"The Current User ID is"+user.getUId(),Toast.LENGTH_LONG).show();
                    if (btnRsvp.getText().toString().equalsIgnoreCase(getString(R.string.rsvp_button))) {
                        //Toast.makeText(getApplicationContext(),"Calling RSVP",Toast.LENGTH_LONG).show();
                        obClient.postRsvpToEvent(selectedEvent.eventId, user.getUId(), rsvpResponseHandler);

                    } else if (btnRsvp.getText().toString().equalsIgnoreCase(getString(R.string.un_rsvp_button))) {
                        //Toast.makeText(getApplicationContext(),"Calling unRSVP",Toast.LENGTH_LONG).show();
                        obClient.postUnRsvpToEvent(selectedEvent.eventId, user.getUId(), unRsvpResponseHandler);
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

        /*
        This function is called to add the even information to the calendar
         */
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
        /*
        This method is called when user decides to add reminders about event.
         */
//        ivAddReminder.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //Toast.makeText(getApplicationContext(),"Adding notification",Toast.LENGTH_LONG).show();
//                addNotification();
//
//           }
//        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.event_info, menu);
        return true;
    }

    /*
    * Called when the Activity becomes visible.
    */
    @Override
    protected void onStart() {
        super.onStart();
        // Connect the client.
        if (isGooglePlayServicesAvailable()) {
            mLocationClient.connect();
        }

    }

    /*
     * Called when the Activity is no longer visible.
     */
    @Override
    protected void onStop() {
        // Disconnecting the client invalidates it.
        mLocationClient.disconnect();
        super.onStop();
    }

    /*
    * Handle results returned to the FragmentActivity by Google Play services
    */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Decide what to do based on the original request code
        switch (requestCode) {

            case CONNECTION_FAILURE_RESOLUTION_REQUEST:
                //If the result code is Activity.RESULT_OK, try to connect again
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        mLocationClient.connect();
                        break;
                }
        }
    }

    /*
    Google play service call backs
     */
    /*
     * Called by Location Services when the request to connect the client
     * finishes successfully. At this point, you can request the current
     * location or start periodic updates
     */
    @Override
    public void onConnected(Bundle dataBundle) {
        // Display the connection status
        Location location = mLocationClient.getLastLocation();
        if (location != null) {
            //Toast.makeText(this, "GPS location was found!", Toast.LENGTH_SHORT).show();
            //LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            //CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 17);
            //map.animateCamera(cameraUpdate);
        } else {
            // Toast.makeText(this, "Current location was null, enable GPS on emulator!", Toast.LENGTH_SHORT).show();
        }
    }

    /*
     * Called by Location Services if the connection to the location client
     * drops because of an error.
     */
    @Override
    public void onDisconnected() {
        // Display the connection status
        Toast.makeText(this, "Disconnected. Please re-connect.", Toast.LENGTH_SHORT).show();
    }

    /*
     * Called by Location Services if the attempt to Location Services fails.
     */
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
		/*
		 * Google Play services can resolve some errors it detects. If the error
		 * has a resolution, try sending an Intent to start a Google Play
		 * services activity that can resolve error.
		 */
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(this,
                        CONNECTION_FAILURE_RESOLUTION_REQUEST);
				/*
				 * Thrown if Google Play services canceled the original
				 * PendingIntent
				 */
            } catch (IntentSender.SendIntentException e) {
                // Log the error
                e.printStackTrace();
            }
        } else {
            Toast.makeText(getApplicationContext(),
                    "Sorry. Location services not available to you", Toast.LENGTH_LONG).show();
        }
    }

    private boolean isGooglePlayServicesAvailable() {
        // Check that Google Play services is available
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        // If Google Play services is available
        if (ConnectionResult.SUCCESS == resultCode) {
            // In debug mode, log the status
            Log.d("Location Updates", "Google Play services is available.");
            return true;
        } else {
            // Get the error dialog from Google Play services
            Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                    CONNECTION_FAILURE_RESOLUTION_REQUEST);

            // If Google Play services can provide an error dialog
            if (errorDialog != null) {
                // Create a new DialogFragment for the error dialog
                ErrorDialogFragment errorFragment = new ErrorDialogFragment();
                errorFragment.setDialog(errorDialog);
                errorFragment.show(getSupportFragmentManager(), "Location Updates");
            }

            return false;
        }
    }

    // Define a DialogFragment that displays the error dialog
    public static class ErrorDialogFragment extends DialogFragment {

        // Global field to contain the error dialog
        private Dialog mDialog;

        // Default constructor. Sets the dialog field to null
        public ErrorDialogFragment() {
            super();
            mDialog = null;
        }

        // Set the dialog to display
        public void setDialog(Dialog dialog) {
            mDialog = dialog;
        }

        // Return a Dialog to the DialogFragment.
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return mDialog;
        }


    }

    private class UpdateMapsFragment extends AsyncTask<String, Void, String> {
        Address eventAddress = null;
        @Override
        protected String doInBackground(String... params) {
            Log.i(TAG, "Event address is " + updatedEvent.getEventAddress());
            Log.i(TAG, "Is Geocode present " + obGeoCoder.isPresent());
            eventAddress = OneBrickGeoCoder.getAddressFromLocationName(updatedEvent.getEventAddress());
            Log.i(TAG, "Geocoded Event address is " + eventAddress);
            return null;
        }


        @Override
        protected void onPostExecute(String result) {
            if (eventAddress != null) {
                lat = eventAddress.getLatitude();
                lng = eventAddress.getLongitude();

                marker = new MarkerOptions()
                        .position(new LatLng(lat, lng))
                        .title("Event Location");
                map.addMarker(marker);

         /*
            Setting up onClick listener on the map
            which when clicked the user will be taken to a new
            activity where he will see the map in a might bigger screen
         */
                map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng latLng) {
                        //Toast.makeText(getBaseContext(),"Map is clicked "+latLng,Toast.LENGTH_LONG).show();
                        Intent eventLocationMap = new Intent(getApplicationContext(), EventLocationView.class);
                        eventLocationMap.putExtra("Latitude", lat);
                        eventLocationMap.putExtra("Longitude", lng);
                        eventLocationMap.putExtra("Address", selectedEvent.getEventAddress());
                        startActivity(eventLocationMap);
                        overridePendingTransition(R.anim.right_in, R.anim.left_out);
                    }
                });
                CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 16F);
                map.animateCamera(cu);
            } else {
                Log.e(TAG,"ERROR : Event address is  null ");
                lat = 37.783;
                lng = 122.416;
                marker = new MarkerOptions()
                        .position(new LatLng(lat, lng))
                        .title("Event Location");
                map.addMarker(marker);

                 /*
                    Setting up onClick listener on the map
                    which when clicked the user will be taken to a new
                    activity where he will see the map in a might bigger screen
                 */
                map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng latLng) {
                        //Toast.makeText(getBaseContext(),"Map is clicked "+latLng,Toast.LENGTH_LONG).show();
                        Intent eventLocationMap = new Intent(getApplicationContext(), EventLocationView.class);
                        eventLocationMap.putExtra("Latitude", lat);
                        eventLocationMap.putExtra("Longitude", lng);
                        eventLocationMap.putExtra("Address", selectedEvent.getEventAddress());
                        startActivity(eventLocationMap);
                        overridePendingTransition(R.anim.right_in, R.anim.left_out);
                    }
                });
                CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 16F);
                map.animateCamera(cu);
            }
        }
    }

//    private void addNotification() {
//        Long time = new GregorianCalendar().getTimeInMillis() + 30000;
//
//        // Create an Intent and set the class that will execute when the Alarm triggers. Here we have
//        // specified AlarmReceiver in the Intent. The onReceive() method of this class will execute when the broadcast from your alarm is received.
//        Intent intentAlarm = new Intent(this, DisplayNotificationReceiver.class);
//        Bundle b = new Bundle();
//        b.putString("EventName", updatedEvent.getTitle());
//        b.putString("DisplayMessage", "Have you RSVP-ed yet ?");
//        intentAlarm.putExtra("EventName", updatedEvent.getTitle());
//        intentAlarm.putExtra("DisplayMessage", "Have you RSVP-ed yet! ?");
//
//        // Get the Alarm Service.
//        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//
//        // Set the alarm for a particular time.
//        PendingIntent addRemainder = PendingIntent.getBroadcast(this
//                , this.getUniqueRandomRequestCode()
//                , intentAlarm, PendingIntent.FLAG_ONE_SHOT);
//        alarmManager.set(AlarmManager.RTC_WAKEUP, time, addRemainder);
//        //Toast.makeText(this, "Alarm Scheduled in next 30 sseconds", Toast.LENGTH_LONG).show();
//        FragmentManager fm = getSupportFragmentManager();
//        ReminderAddDialog rad = ReminderAddDialog.newInstance("Add Reminder ?");
//        rad.show(fm, "Add Reminder ?");
//    }

//    public int getUniqueRandomRequestCode() {
//        return (int) (System.currentTimeMillis() % Integer.MAX_VALUE);
//    }

//    private Drawable getBannerDrawable(@NonNull String fileName){
//        // load image
//        try {
//            Drawable drawable = getResources().getDrawable(getResources()
//                    .getIdentifier(fileName, "drawable", getPackageName()));
//            return drawable;
//        }catch(Exception ex) {
//            Log.e(TAG, "error to drawable from file in assets");
//            Log.e(TAG, ex.toString());
//            return null;
//        }
//    }

}
