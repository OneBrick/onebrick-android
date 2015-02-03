package org.onebrick.android.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.AttributeSet;
import android.view.MenuItem;
import android.view.View;

import org.onebrick.android.R;
import org.onebrick.android.listeners.SupportFragmentTabListener;
import org.onebrick.android.fragments.MyPastEventsFragment;
import org.onebrick.android.fragments.MyUpcomingEventsFragment;

public class MyEventsActivity extends ActionBarActivity {

    public static final String UPCOMING_EVENTS_TAG = "upcoming";
    public static final String PAST_EVENTS_TAG = "past_events";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_events);
        setupTabs();
    }

    @Override
    public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
        return super.onCreateView(parent, name, context, attrs);
    }

    private void setupTabs() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.setDisplayShowTitleEnabled(true);

        ActionBar.Tab tab1 = actionBar
                .newTab()
                .setText(R.string.upcoming)
                .setTabListener(new SupportFragmentTabListener<>(R.id.flMyEventsContainer, this,
                        UPCOMING_EVENTS_TAG, MyUpcomingEventsFragment.class));

        actionBar.addTab(tab1);
        actionBar.selectTab(tab1);

        ActionBar.Tab tab2 = actionBar
                .newTab()
                .setText(R.string.past_events)
                .setTabListener(new SupportFragmentTabListener<>(R.id.flMyEventsContainer, this,
                        PAST_EVENTS_TAG, MyPastEventsFragment.class));
        actionBar.addTab(tab2);
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.left_in, R.anim.right_out);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
