package org.onebrick.android.activities;

import android.content.Intent;
import android.content.res.Configuration;
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

    private DrawerLayout dlDrawerLayout;
    private View llDrawer;
    private ListView lvDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private NavigationChapterListAdapter aLvDrawerList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        chaptersList = new ArrayList<Chapter>();

        setupUi();
        setupListeners();
        fetchChapters();
        Intent i = getIntent();
        int chapterId = i.getIntExtra("ChapterId", -1);
        String chapterName = i.getStringExtra("ChapterName");
        Fragment eventListFragment = EventsListFragment.newInstance(chapterName,
                chapterId);
        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction()
                .replace(R.id.fragment_container, eventListFragment)
                .commit();
    }

    private void setupUi() {
        dlDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        llDrawer = findViewById(R.id.rlDrawer);
        mDrawerToggle = setupDrawerToggle();
        dlDrawerLayout.setDrawerListener(mDrawerToggle);
        lvDrawerList = (ListView) findViewById(R.id.lvChapters);
        aLvDrawerList = new NavigationChapterListAdapter(getApplicationContext(),R.layout.drawer_nav_item,chaptersList);
        lvDrawerList.setAdapter(aLvDrawerList);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        findViewById(R.id.tvLogin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                HomeActivity.this.startActivity(intent);

                // on login success change login button to profile view
            }
        });

        findViewById(R.id.ibSetting).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // open setting activity
            }
        });

        findViewById(R.id.tvMyEvents).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent intent = new Intent(HomeActivity.this, MyEventsActivity.class);
                HomeActivity.this.startActivity(intent);

                // on login success change login button to profile view
            }
        });
    }

    private void fetchChapters() {
        JsonHttpResponseHandler responseHandler = new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                chaptersList.clear();
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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_login) {
            // Hye: For now plugin user login here, later I will add to navigation drawer
            return true;
        }
        // The action bar home/up action should open or close the drawer.
        // ActionBarDrawerToggle will take care of this.
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggles
        mDrawerToggle.onConfigurationChanged(newConfig);
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
        dlDrawerLayout.closeDrawer(llDrawer);
    }

    private ActionBarDrawerToggle setupDrawerToggle() {
        return new ActionBarDrawerToggle(this, /* host Activity */
                dlDrawerLayout, /* DrawerLayout object */
                R.drawable.ic_navigation_drawer, /* nav drawer image to replace 'Up' caret */
                R.string.drawer_open, /* "open drawer" description for accessibility */
                R.string.drawer_close /* "close drawer" description for accessibility */
        ) {
            public void onDrawerClosed(View view) {
                // setTitle(getCurrentTitle());
                // call onPrepareOptionsMenu()
                supportInvalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                // setTitle("Navigate");
                // call onPrepareOptionsMenu()
                supportInvalidateOptionsMenu();
            }
        };
    }


}
