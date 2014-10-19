package org.onebrick.android.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONObject;
import org.onebrick.android.LoginManager;
import org.onebrick.android.OneBrickApplication;
import org.onebrick.android.OneBrickClient;
import org.onebrick.android.R;
import org.onebrick.android.models.Event;
import org.onebrick.android.models.User;

import java.io.IOException;
import java.util.List;

public class EventInfoActivity extends FragmentActivity implements
        GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener {


    String eventId;
    private static final String TAG = HomeActivity.class.getName().toString();
    OneBrickClient obClient = OneBrickApplication.getRestClient();
    JsonHttpResponseHandler responseHandler = new JsonHttpResponseHandler(){
        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            super.onSuccess(statusCode, headers, response);
            Log.i("TAG","Success"+response.toString());
            Event updatedEvent = Event.getUpdatedEvent(response);
            updateViews(updatedEvent);
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
            Log.i("TAG","Json Request to fetch event info failed");
            super.onFailure(statusCode, headers, throwable, errorResponse);
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
            Log.i("TAG","FAIL "+responseString);
            super.onFailure(statusCode, headers, responseString, throwable);
        }

    };


    JsonHttpResponseHandler rsvpResponseHandler = new JsonHttpResponseHandler(){
        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            super.onSuccess(statusCode, headers, response);
            Log.i("TAG","RSVP Success"+response.toString());
            Drawable unrsvp = getResources().getDrawable(R.drawable.ic_unrsvp_50dip);
            btnRsvp.setCompoundDrawablesWithIntrinsicBounds(unrsvp, null, null, null);
            btnRsvp.setText("UnRsvp");
            ivRsvpInfo.setImageDrawable(
                    getResources().getDrawable(R.drawable.ic_rsvp_yes_info_75dip));
            tvRsvpInfo.setText("All set, You have Rsvp-ed to this event!");

        }

        @Override
        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
            Log.i("TAG","Json Request to fetch event info failed");
            super.onFailure(statusCode, headers, throwable, errorResponse);
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
            Log.i("TAG","FAIL "+responseString);
            super.onFailure(statusCode, headers, responseString, throwable);
        }

    };

    JsonHttpResponseHandler unRsvpResponseHandler = new JsonHttpResponseHandler(){
        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            super.onSuccess(statusCode, headers, response);
            Log.i("TAG","RSVP Un-Success"+response.toString());
            Drawable rsvp = getResources().getDrawable(R.drawable.ic_rsvp_50dip);
            //btnRsvp.setCompoundDrawables(unrsvp,null,null,null);
            btnRsvp.setCompoundDrawablesWithIntrinsicBounds(rsvp, null, null, null);
            btnRsvp.setText("Rsvp");
            ivRsvpInfo.setImageDrawable(
                    getResources().getDrawable(R.drawable.ic_rsvp_info_75dip));
            tvRsvpInfo.setText("You have not Rsvp-ed to this event yet.");

        }

        @Override
        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
            Log.i("TAG","Json Request to fetch event info failed");
            super.onFailure(statusCode, headers, throwable, errorResponse);
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
            Log.i("TAG","FAIL "+responseString);
            super.onFailure(statusCode, headers, responseString, throwable);
        }

    };

    TextView tvEventName;
    TextView tvEventDateTime;
    TextView tvEventBrief;
    TextView tvEventLocation;
    TextView tvLearnMore;
    Button btnRsvp;
    ImageView ivRsvpInfo;
    TextView tvRsvpInfo;

    private SupportMapFragment mapFragment;
    private GoogleMap map;
    private Geocoder geocoder;
    private LocationClient mLocationClient;
    private MarkerOptions marker;
    Event selectedEvent;
    LoginManager loginMgr;
    User user;
    double lat;
    double lng;
    OneBrickClient obclient;
    BitmapDescriptor customMarker;
    /*
     * Define a request code to send to Google Play services This code is
     * returned in Activity.onActivityResult
     */
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    private void updateViews(Event updatedEvent) {
        selectedEvent = updatedEvent;
                tvEventName.setText(updatedEvent.getTitle());
        tvEventDateTime.setText(updatedEvent.getEventStartDate()
                +" to "
                +updatedEvent.getEventEndDate());
        tvEventBrief.setText(updatedEvent.getEventDescription());
        tvEventLocation.setText(updatedEvent.getEventAddress());
        List<Address> eventAddress = null;
        try {
            Log.i(TAG,"Event address is "+updatedEvent.getEventAddress());
            Log.i(TAG,"Is Geocode present "+geocoder.isPresent());
            eventAddress  = geocoder.getFromLocationName(updatedEvent.getEventAddress(),1);
            //eventAddress  = geocoder.getFromLocationName("1600 Amphitheatre Parkway, Mountain View, CA",1);

            Log.i(TAG,"Geocoded Event address is "+eventAddress);
        } catch (IOException e) {
            e.printStackTrace();
        }
        lat = eventAddress.get(0).getLatitude();
        lng = eventAddress.get(0).getLongitude();

        // Toast.makeText(this, "LAT/LONG " + latitude + " "+longitude, Toast.LENGTH_LONG).show();
        // create marker

        MarkerOptions marker = new MarkerOptions()
                .position(new LatLng(lat, lng))
                .icon(customMarker)
                .title("Event Location");
        map.addMarker(marker);
        CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(marker.getPosition(),16F);
        map.animateCamera(cu);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_info);

        customMarker = BitmapDescriptorFactory.fromResource(R.drawable.ic_custom_marker);
        tvEventName = (TextView) findViewById(R.id.tvEventName);
        tvEventDateTime = (TextView) findViewById(R.id.tvEventTime);
        tvEventBrief = (TextView) findViewById(R.id.tvEventBrief);
        tvEventLocation = (TextView) findViewById(R.id.tvEventLocation);
        tvLearnMore = (TextView) findViewById(R.id.tvLearnMore);

        btnRsvp = (Button) findViewById(R.id.btnRsvp);
        tvRsvpInfo = (TextView) findViewById(R.id.tvRsvpInfo);
        ivRsvpInfo = (ImageView) findViewById(R.id.ivRsvpPeople);

        loginMgr = LoginManager.getInstance(getApplicationContext());
        obclient = OneBrickApplication.getRestClient();
        Intent eventInfo = getIntent();
        eventId = eventInfo.getStringExtra("EventId");

        // Setting the action bar title
        getActionBar().setTitle("Event Info");
        // Using GeoCoder so not using he api call
        obClient.getEventInfo(eventId, responseHandler);
        geocoder = new Geocoder(this);
        /*
        Loading map
         */
        mLocationClient = new LocationClient(this, this, this);
        mapFragment =
                ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapFrame));
        if (mapFragment != null) {
            map = mapFragment.getMap();
            if (map != null) {

            } else {
               // Toast.makeText(this, "Error - Map was null!!", Toast.LENGTH_LONG).show();
            }
        } else {
           // Toast.makeText(this, "Error - Map Fragment was null!!", Toast.LENGTH_LONG).show();
        }
        setupListeners();
    }

    private void setupListeners() {
        /*
        Setting up onClick listener on Learn more
        which when clicked the user will be taken to a new
        activity where he will get more detail description on the event.
         */
        tvLearnMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent eventDetails = new Intent(getApplicationContext(), EventDescription.class);
                eventDetails.putExtra("Details",""+selectedEvent.getEventDescription());
                startActivity(eventDetails);
            }
        });

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
                eventLocationMap.putExtra("Latitude",lat);
                eventLocationMap.putExtra("Longitude",lng);
                eventLocationMap.putExtra("Address", selectedEvent.getEventAddress());
                startActivity(eventLocationMap);
            }
        });

        btnRsvp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            if(!loginMgr.isLoggedIn()) {
                /*
                If the user is not logged in the prompting the use to login
                 */
                Intent loingActivity = new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(loingActivity);
            } else {
                user = loginMgr.getCurrentUser();
                Toast.makeText(getApplicationContext(),"The Current User ID is"+user.getUId(),Toast.LENGTH_LONG).show();
                if(btnRsvp.getText().toString().equalsIgnoreCase("RSVP")) {

                    obclient.postRsvpToEvent(selectedEvent.eventId, user.getUId(),rsvpResponseHandler);

                } else if (btnRsvp.getText().toString().equalsIgnoreCase("UnRSVP")) {
                    obclient.postUnRsvpToEvent(selectedEvent.eventId, user.getUId(), unRsvpResponseHandler);

                } else {
                }
            }

            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.event_info, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
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
			/*
			 * If the result code is Activity.RESULT_OK, try to connect again
			 */
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
            Toast.makeText(this, "GPS location was found!", Toast.LENGTH_SHORT).show();
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 17);
            map.animateCamera(cameraUpdate);
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

}
