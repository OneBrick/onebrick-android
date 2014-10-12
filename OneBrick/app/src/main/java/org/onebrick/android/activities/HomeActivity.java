package org.onebrick.android.activities;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONObject;
import org.onebrick.android.OneBrickApplication;
import org.onebrick.android.OneBrickClient;
import org.onebrick.android.R;
import org.onebrick.android.adapters.NavigationChapterListAdapter;
import org.onebrick.android.fragments.EventsListFragment;
import org.onebrick.android.models.Chapter;

import java.util.ArrayList;

public class HomeActivity extends FragmentActivity {
    private static final String TAG = HomeActivity.class.getName().toString();
    OneBrickClient obClient = OneBrickApplication.getRestClient();
    ArrayList<Chapter> chaptersList;
    ActionBar actionBar;

    private DrawerLayout dlDrawerLayout;
    private ListView lvDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private NavigationChapterListAdapter aLvDrawerList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        chaptersList = new ArrayList<Chapter>();
        JsonHttpResponseHandler responseHandler = new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                chaptersList.addAll(Chapter.getChapterListFromJsonObject(response));
                aLvDrawerList.addAll(chaptersList);
                aLvDrawerList.notifyDataSetChanged();
                Log.i(TAG,""+chaptersList);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.i(TAG,"Api called failed!");
            }
        };
        obClient.getChapters(responseHandler);
        actionBar = getActionBar();
        dlDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        lvDrawerList = (ListView) findViewById(R.id.lvDrawer);
        aLvDrawerList = new NavigationChapterListAdapter(getApplicationContext(),R.layout.drawer_nav_item,chaptersList);
        lvDrawerList.setAdapter(aLvDrawerList);
        setupListeners();
    }

    


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        setupActionBar();
        return true;
    }

    private void setupActionBar() {
        actionBar.setBackgroundDrawable(OneBrickApplication.obColorBlue);
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

    public void setupListeners() {
        lvDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Chapter ch = aLvDrawerList.getItem(position);
                lvDrawerList.setItemChecked(position, true);
                displayEventsInChapter(ch);
            }
        });
    }

    private void displayEventsInChapter(Chapter ch) {
        Fragment eventListFragment = EventsListFragment.newInstance(ch.getChapterName(),
                ch.getChapterId());

        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction()
                .replace(R.id.fragment_container, eventListFragment)
                .commit();
        dlDrawerLayout.closeDrawer(lvDrawerList);
    }
}
