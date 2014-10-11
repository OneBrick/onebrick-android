package org.onebrick.android.activities;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.onebrick.android.OneBrickApplication;
import org.onebrick.android.OneBrickClient;
import org.onebrick.android.R;
import org.onebrick.android.adapters.EventsListAdapter;
import org.onebrick.android.models.Event;

import java.util.ArrayList;


public class EventsActivity extends Activity {
    private OneBrickClient client;
    private EventsListAdapter adapter;

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
        client = OneBrickApplication.getRestClient();
        String chapterId = null;
        client.getEventsList(chapterId, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(JSONArray array) {
                //Log.d("debug", json.toString());
                ArrayList<Event> events = Event.fromJSONArray(array);
                adapter.addAll(events);
            }
            @Override
            public void onFailure(Throwable e, String s) {
                Log.d("debug fail", e.toString());
                Log.d("debug fail", s);
            }
            @Override
            protected void handleFailureMessage(Throwable e, String s) {
                Log.d("debug fail", e.toString());
                Log.d("debug fail", s);
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
