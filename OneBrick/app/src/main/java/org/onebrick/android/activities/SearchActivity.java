package org.onebrick.android.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.fortysevendeg.swipelistview.SwipeListView;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.onebrick.android.OneBrickApplication;
import org.onebrick.android.OneBrickClient;
import org.onebrick.android.R;
import org.onebrick.android.adapters.EventSearchListAdapter;
import org.onebrick.android.models.Event;

import java.util.ArrayList;

public class SearchActivity extends Activity {
    SwipeListView eventSearchList;
    EventSearchListAdapter aEventSearchList;
    ArrayList<Event> eventList;
    int chapterId;
    String chapterName;
    OneBrickClient obClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seach);
        getActionBar().setTitle("Search");
        obClient = OneBrickApplication.getRestClient();
        eventList = new ArrayList<Event>();
        eventSearchList = (SwipeListView) findViewById(R.id.lvEventSearchList);
        aEventSearchList = new EventSearchListAdapter(this,eventList);
        Intent chapterInfo = getIntent();
        chapterId = chapterInfo.getIntExtra("chapterId", -1);
        chapterName = chapterInfo.getStringExtra("chapterName");
        eventSearchList.setAdapter(aEventSearchList);
        //Toast.makeText(this,"Getting events for chapter "+chapterName+" "+chapterId,Toast.LENGTH_LONG).show();
        /*
        Event e = new Event();
        Event e1 = new Event();
        Event e2 = new Event();
        Event e3 = new Event();
        Event e4 = new Event();
        Event e5 = new Event();
        Event e6 = new Event();
        Event e7 = new Event();

        aEventSearchList.add(e);
        aEventSearchList.add(e1);
        aEventSearchList.add(e2);
        aEventSearchList.add(e3);
        aEventSearchList.add(e4);
        aEventSearchList.add(e5);
        aEventSearchList.add(e6);
        aEventSearchList.add(e7);*/

        obClient.getEventsList(chapterId, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
            }

            @Override
            public void onFinish() {
                super.onFinish();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Log.i("INFO", "callback success"); // logcat log
                aEventSearchList.clear();
                if (response != null){
                    aEventSearchList.addAll(Event.fromJSONArray(response, chapterId));
                    aEventSearchList.notifyDataSetChanged();
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers,
                                  String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Log.e("ERROR", responseString);
                Log.e("ERROR", throwable.toString());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers,
                                  Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Log.e("ERROR", errorResponse.toString());
                Log.e("ERROR", throwable.toString());
            }

        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.seach, menu);
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
