package org.onebrick.android.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.fortysevendeg.swipelistview.SwipeListView;

import org.onebrick.android.R;
import org.onebrick.android.adapters.EventSearchListAdapter;

public class SearchActivity extends Activity {
    SwipeListView eventSearchList;
    EventSearchListAdapter aEventSearchList;
    //ArrayList<Events> eventList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seach);
        eventSearchList = (SwipeListView) findViewById(R.id.lvEventSearchList);
        //aEventSearchList = new EventSearchListAdapter(this,R.id.);
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
