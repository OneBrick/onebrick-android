package org.onebrick.android.activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.onebrick.android.R;
import org.onebrick.android.adapters.EventsListAdapter;
import org.onebrick.android.models.Event;

import java.util.ArrayList;


public class EventsActivity extends ActionBarActivity {
    //private OneBrickClient client;
    private EventsListAdapter adapter;
    //private ArrayList<Event> events;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);
        setUpView();
    }

    private void setUpView(){
        ArrayList<Event> arrayOfUsers = new ArrayList<Event>();
        adapter = new EventsListAdapter(this, arrayOfUsers);
        ListView listView = (ListView) findViewById(R.id.lvEvents);
        listView.setAdapter(adapter);
        populateJSONData();
    }

    private void populateJSONData() {
        //client = OneBrickApplication.getRestClient();
        String chapterId = null;
//        client.getEventsList(chapterId, new JsonHttpResponseHandler(){
//
//            public void onSuccess(JSONArray array) {
//                //Log.d("debug", json.toString());
//                ArrayList<Event> events = Event.fromJSONArray(array);
//                adapter.addAll(events);
//            }
//            public void onFailure(Throwable e, String s) {
//                Log.d("debug fail", e.toString());
//                Log.d("debug fail", s);
//            }
//            protected void handleFailureMessage(Throwable e, String s) {
//                Log.d("debug fail", e.toString());
//                Log.d("debug fail", s);
//            }
//        });
        AsyncHttpClient client = new AsyncHttpClient();
        client.get("http://dev-v3.gotpantheon.com/noauth/event.json?chapter=101",new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers,
                                  JSONObject response) {

            }

            @Override
            public void onFailure(int statusCode, Header[] headers,
                                  String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                super.onSuccess(statusCode, headers, responseString);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);
                // once success response comes back
                Log.i("INFO", response.toString()); // logcat log

                if (response != null){
//                    if (events != null){
//                        events.clear();
//                    }else{
//                        events = new ArrayList<Event>();
//                    }
                    JSONObject eventJsonObject;
                    for (int i = 0; i < response.length(); i++) {
                        Event event = null;
                        try {
                            eventJsonObject = (JSONObject) response.get(i);
                            event = new Event();
                            event.setTitle(eventJsonObject.optString("title"));
                            event.setEventStartDate(eventJsonObject.optString("field_event_date_value"));
                            event.setEventEndDate(eventJsonObject.optString("field_event_date_value2"));
                            adapter.add(event);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }

                // notify adapter so new data is displayed
                adapter.notifyDataSetChanged();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.events, menu);
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
}
