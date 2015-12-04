package org.onebrick.android.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.Toast;

import com.squareup.otto.Subscribe;

import org.onebrick.android.R;
import org.onebrick.android.core.OneBrickApplication;
import org.onebrick.android.core.OneBrickRESTClient;
import org.onebrick.android.events.FetchMyEventsEvent;
import org.onebrick.android.events.Status;
import org.onebrick.android.fragments.MyPastEventsFragment;
import org.onebrick.android.fragments.MyUpcomingEventsFragment;

public class MyEventsActivity extends AppCompatActivity {

    public static final String UPCOMING_EVENTS_TAG = "upcoming";
    public static final String PAST_EVENTS_TAG = "past_events";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_events);
        setupTabs();

        if (savedInstanceState == null) {
            OneBrickRESTClient.getInstance().requestMyEvents();
        }
        OneBrickApplication.getInstance().getBus().register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        OneBrickApplication.getInstance().getBus().unregister(this);
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

    public static class SupportFragmentTabListener<T extends Fragment> implements ActionBar.TabListener {
        private Fragment mFragment;
        private final FragmentActivity mActivity;
        private final String mTag;
        private final Class<T> mClass;
        private final int mfragmentContainerId;
        private final Bundle mfragmentArgs;

        public SupportFragmentTabListener(FragmentActivity activity, String tag, Class<T> clz) {
            mActivity = activity;
            mTag = tag;
            mClass = clz;
            mfragmentContainerId = android.R.id.content;
            mfragmentArgs = new Bundle();
        }

        public SupportFragmentTabListener(int fragmentContainerId, FragmentActivity activity, String tag, Class<T> clz) {
            mActivity = activity;
            mTag = tag;
            mClass = clz;
            mfragmentContainerId = fragmentContainerId;
            mfragmentArgs = new Bundle();
        }

        // This version supports specifying the container to replace with fragment content and fragment args
        // new SupportFragmentTabListener<SomeFragment>(R.id.flContent, this, "first", SomeFragment.class, myFragmentArgs))
        public SupportFragmentTabListener(int fragmentContainerId, FragmentActivity activity,
                                          String tag, Class<T> clz, Bundle args) {
            mActivity = activity;
            mTag = tag;
            mClass = clz;
            mfragmentContainerId = fragmentContainerId;
            mfragmentArgs = args;
        }

        /* The following are each of the ActionBar.TabListener callbacks */

        public void onTabSelected(ActionBar.Tab tab, FragmentTransaction sft) {
            // Check if the fragment is already initialized
            if (mFragment == null) {
                // If not, instantiate and add it to the activity
                mFragment = Fragment.instantiate(mActivity, mClass.getName(), mfragmentArgs);
                sft.add(mfragmentContainerId, mFragment, mTag);
            } else {
                // If it exists, simply attach it in order to show it
                sft.attach(mFragment);
            }
        }

        public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction sft) {
            if (mFragment != null) {
                // Detach the fragment, because another one is being attached
                sft.detach(mFragment);
            }
        }

        public void onTabReselected(ActionBar.Tab tab, FragmentTransaction sft) {
            // User selected the already selected tab. Usually do nothing.
        }
    }

    @Subscribe
    public void onFetchMyEventsEvent(FetchMyEventsEvent event) {
        if (event.status == Status.NO_NETWORK) {
            Toast.makeText(this, R.string.no_network, Toast.LENGTH_LONG).show();
        } else if (event.status == Status.FAILED) {
            Toast.makeText(this, R.string.failed_to_fetch_my_events, Toast.LENGTH_LONG).show();
        }
    }
}
