package org.onebrick.android.cards;

import android.app.Activity;
import android.content.Context;
import android.content.IntentSender;
import android.location.Address;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.onebrick.android.R;
import org.onebrick.android.helpers.GeoCodeHelper;
import org.onebrick.android.models.Event;

import java.util.List;

public class MapCard extends EventCard implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "MapCard";
    private MapFragment mapFragment;
    private GoogleMap map;
    private GoogleApiClient mGoogleApiClient;
    private String eventLocation;
    //Define a request code to send to Google Play services
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private static Context context;

    public MapCard(Context context, @NonNull Event event) {
        super(context, event);
        this.context = context;
    }

    @Override
    public View initView(@NonNull ViewGroup parent) {
        initView(parent, R.layout.card_event_detail_map);

        if (!mEvent.getAddress().isEmpty() ){
            eventLocation = mEvent.getAddress();
            Log.d("TAG", "event location: " + eventLocation);
            setupMap();
        }
        return mView;
    }

    private void setupMap() {
        if (context instanceof Activity){
            mapFragment = (MapFragment) ((Activity) context).getFragmentManager().findFragmentById(R.id.map_frame);
            if (mapFragment != null) {
                mapFragment.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(GoogleMap googleMap) {
                        loadMap(googleMap);
                    }
                });
            }else{
                Log.d(TAG, "Error - Map Fragment was null.");
            }
        } else {
            Log.d(TAG, "Error - context is not activity.");
        }
    }

    protected void loadMap(@NonNull GoogleMap googleMap) {
        map = googleMap;
        map.setMyLocationEnabled(true);
        // Now that map has loaded, let's get our location!
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).build();
        connectClient();
    }

    protected void connectClient() {
        if (isGooglePlayServicesAvailable() && mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    private boolean isGooglePlayServicesAvailable() {
        // Check that Google Play services is available
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);
        if (ConnectionResult.SUCCESS == resultCode) {
            Log.d(TAG, "Google Play services is available.");
            return true;
        } else {
            Log.d(TAG, "Google Play services is not available.");
            return false;
        }
    }

    /*
     * Called by Location Services when the request to connect the client
     * finishes successfully. At this point, you can request the current
     * location or start periodic updates
     */
    @Override
    public void onConnected(Bundle dataBundle) {
        List<Address> geoCode = GeoCodeHelper.getGeoCode(context, eventLocation);
        if (geoCode != null && !geoCode.isEmpty() ){
            LatLng location = new LatLng(
                    geoCode.get(0).getLatitude(), geoCode.get(0).getLongitude());
            map.setMyLocationEnabled(true);
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 13));
            map.addMarker(new MarkerOptions()
                    .title(eventLocation)
                    .position(location));
        }
    }

    /*
     * Called by Location Services if the connection to the location client
     * drops because of an error.
     */
    @Override
    public void onConnectionSuspended(int i) {
        if (i == CAUSE_SERVICE_DISCONNECTED) {
            Toast.makeText(context, "Disconnected. Please re-connect.", Toast.LENGTH_SHORT).show();
        } else if (i == CAUSE_NETWORK_LOST) {
            Toast.makeText(context, "Network lost. Please re-connect.", Toast.LENGTH_SHORT).show();
        }
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
                connectionResult.startResolutionForResult((Activity) context,
                        CONNECTION_FAILURE_RESOLUTION_REQUEST);
            } catch (IntentSender.SendIntentException e) {
                Log.e(TAG, "Connection failed");
                e.printStackTrace();
            }
        } else {
            Toast.makeText(context,
                    "Sorry. Location services not available to you", Toast.LENGTH_LONG).show();
        }
    }
}
