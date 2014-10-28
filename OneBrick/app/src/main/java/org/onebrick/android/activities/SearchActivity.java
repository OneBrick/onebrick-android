package org.onebrick.android.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.Toast;

import com.fortysevendeg.swipelistview.SwipeListView;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;
import org.onebrick.android.OneBrickApplication;
import org.onebrick.android.OneBrickClient;
import org.onebrick.android.R;
import org.onebrick.android.adapters.EventSearchListAdapter;
import org.onebrick.android.models.Event;

import java.util.ArrayList;

public class SearchActivity extends Activity implements OnQueryTextListener {
    SwipeListView eventSearchList;
    EventSearchListAdapter aEventSearchList;
    ArrayList<Event> eventList;
    int chapterId;
    String chapterName;
    OneBrickClient obClient;
    String searchQuery;
    ActionBar actionBar;
    MenuInflater menuInflater;
    MenuItem searchItem;
    SearchView searchView;
    ProgressBar progressBar;

    JsonHttpResponseHandler searchResultHandler = new JsonHttpResponseHandler() {
        @Override
        public void onStart() {

            super.onStart();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        public void onFinish() {
            super.onFinish();
            progressBar.setVisibility(View.INVISIBLE);
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
            Log.i("INFO", "callback success"); // logcat log
            aEventSearchList.clear();
            if (response != null){
                aEventSearchList.addAll(Event.fromJSONArray(response, chapterId));
                if(aEventSearchList.isEmpty()){
                    aEventSearchList.clear();
                    Event e = new Event();
                    e.setTitle("Error");
                    aEventSearchList.add(e);
                }
                aEventSearchList.notifyDataSetChanged();
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
            super.onFailure(statusCode, headers, throwable, errorResponse);
            aEventSearchList.clear();
            Event e = new Event();
            e.setTitle("Error");
            aEventSearchList.add(e);
            aEventSearchList.notifyDataSetChanged();
        }

        @Override
        public void onFailure(int statusCode, Header[] headers,
                              String responseString, Throwable throwable) {
            super.onFailure(statusCode, headers, responseString, throwable);
            Log.e("ERROR", responseString);
            Log.e("ERROR", throwable.toString());
            aEventSearchList.clear();
            Event e = new Event();
            e.setTitle("Error");
            aEventSearchList.add(e);
            aEventSearchList.notifyDataSetChanged();
        }

        @Override
        public void onFailure(int statusCode, Header[] headers,
                              Throwable throwable, JSONArray errorResponse) {
            super.onFailure(statusCode, headers, throwable, errorResponse);
            Log.e("ERROR", errorResponse.toString());
            Log.e("ERROR", throwable.toString());
            aEventSearchList.clear();
            Event e = new Event();
            e.setTitle("Error");
            aEventSearchList.add(e);
            aEventSearchList.notifyDataSetChanged();
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        getActionBar().setTitle("Search");
        obClient = OneBrickApplication.getRestClient();
        eventList = new ArrayList<Event>();
        eventSearchList = (SwipeListView) findViewById(R.id.lvEventSearchList);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        aEventSearchList = new EventSearchListAdapter(this,eventList);
        Intent chapterInfo = getIntent();
        chapterId = chapterInfo.getIntExtra("chapterId", -1);
        chapterName = chapterInfo.getStringExtra("chapterName");
        eventSearchList.setAdapter(aEventSearchList);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        actionBar = getActionBar();
        menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.search, menu);
        actionBar.setStackedBackgroundDrawable(new ColorDrawable(android.R.color.white));
        actionBar.setDisplayHomeAsUpEnabled(true);
        searchItem = menu.findItem(R.id.miSearch);
        searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(this);
        searchItem.expandActionView();
        return true;
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.left_in, R.anim.right_out);
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
    public boolean onQueryTextSubmit(String query) {
        searchQuery = query;
        Toast.makeText(this, "Searching for "+query, Toast.LENGTH_SHORT).show();
        //searchTwitter(query);
        obClient.searchForEvents(chapterId, query, searchResultHandler);
        searchItem.collapseActionView();
        return true;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        return false;
    }
}
