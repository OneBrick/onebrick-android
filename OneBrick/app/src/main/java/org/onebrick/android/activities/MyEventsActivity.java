package org.onebrick.android.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import org.onebrick.android.R;
import org.onebrick.android.SupportFragmentTabListener;
import org.onebrick.android.fragments.MyPastEventsFragment;
import org.onebrick.android.fragments.MyUpcomingEventsFragment;

public class MyEventsActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_events);
        setupTabs();
    }

    private void setupTabs() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.setDisplayShowTitleEnabled(true);

        ActionBar.Tab tab1 = actionBar
                .newTab()
                .setText(R.string.upcoming)
                .setTabListener(new SupportFragmentTabListener<MyUpcomingEventsFragment>(R.id.flContainer, this,
                        "upcoming", MyUpcomingEventsFragment.class));

        actionBar.addTab(tab1);
        actionBar.selectTab(tab1);

        ActionBar.Tab tab2 = actionBar
                .newTab()
                .setText(R.string.past_events)
                .setTabListener(new SupportFragmentTabListener<MyPastEventsFragment>(R.id.flContainer, this,
                        "past_events", MyPastEventsFragment.class));
        actionBar.addTab(tab2);
    }

    public void onShareThis(){
        Intent intent=new Intent(android.content.Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        // Add data to the intent, the receiving app will decide what to do with it.
        intent.putExtra(Intent.EXTRA_SUBJECT, "Some Subject Line");
        intent.putExtra(Intent.EXTRA_TEXT, "Body of the message!");
        startActivity(Intent.createChooser(intent, "share"));
    }

    public void onShareThis(){
        Intent intent=new Intent(android.content.Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        // Add data to the intent, the receiving app will decide what to do with it.
        intent.putExtra(Intent.EXTRA_SUBJECT, "Some Subject Line");
        intent.putExtra(Intent.EXTRA_TEXT, "Body of the message!");
        startActivity(Intent.createChooser(intent, "share"));
    }
}
