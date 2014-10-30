package org.onebrick.android.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;
import org.onebrick.android.OneBrickApplication;
import org.onebrick.android.OneBrickClient;
import org.onebrick.android.R;
import org.onebrick.android.adapters.CalendarAdapter;
import org.onebrick.android.models.Event;

import java.util.ArrayList;

import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

public class CalendarViewActivity extends Activity {
    StickyListHeadersListView calendar;
    CalendarAdapter aCalendar;
    ArrayList<Event> eventList;
    int chapterId;
    String chapterName;
    OneBrickClient obClient;
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar_view);
        calendar = (StickyListHeadersListView) findViewById(R.id.calendarList);
        progressBar = (ProgressBar) findViewById(R.id.calProgressBar);
        Intent chapterInfo = getIntent();
        chapterId = chapterInfo.getIntExtra("chapterId", -1);
        chapterName = chapterInfo.getStringExtra("chapterName");
        eventList = new ArrayList<Event>();
        aCalendar = new CalendarAdapter(this,eventList);
        obClient = OneBrickApplication.getRestClient();
        calendar.setAdapter(aCalendar);
        getActionBar().setTitle(""+chapterName+"'s"+" Calendar");
        obClient.getEventsList(chapterId, -1, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                progressBar.setVisibility(ProgressBar.VISIBLE);
            }

            @Override
            public void onFinish() {
                super.onFinish();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Log.i("INFO", "callback success"); // logcat log
                eventList.clear();
                if (response != null){
                    eventList.addAll(Event.fromJSONArray(response, chapterId));
                    aCalendar.setDatesFromEventList(eventList);
                    aCalendar.setSectionIndicesFromEventList(eventList);
                    aCalendar.notifyDataSetChanged();
                    progressBar.setVisibility(ProgressBar.GONE);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers,
                                  String responseString, Throwable throwable) {
                //super.onFailure(statusCode, headers, responseString, throwable);
                //Toast.makeText(getActivity(),"API Called error",Toast.LENGTH_SHORT).show();
                //aEventList.clear();
                //Event e = new Event();
                //e.setTitle("Error");
                //aEventList.add(e);
                //aEventList.notifyDataSetChanged();
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
        getMenuInflater().inflate(R.menu.calendar_view, menu);
        return true;
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
}
