package org.onebrick.android.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import org.onebrick.android.OneBrickApplication;
import org.onebrick.android.R;
import org.onebrick.android.fragments.HomeEventsFragment;
import org.onebrick.android.fragments.SelectChapterFragment;
import org.onebrick.android.helpers.LoginManager;
import org.onebrick.android.models.Chapter;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class HomeActivity extends ActionBarActivity
        implements SelectChapterFragment.OnSelectChapterFragmentListener {

    private static final String TAG = HomeActivity.class.getName().toString();
    public static final String SELECT_CHAPTER_FRAGMENT_TAG = "select_chapter";

    @InjectView(R.id.pbSelectChapter) ProgressBar pbSelectChapter;
    @InjectView(R.id.drawer_layout) DrawerLayout dlDrawerLayout;
    @InjectView(R.id.tvName) TextView tvName;
    @InjectView(R.id.ivUserPic) ImageView ivProfile;
    @InjectView(R.id.tvMyEvents) TextView tvMyEvents;
    @InjectView(R.id.tvLogin) TextView tvLogin;
    @InjectView(R.id.tvSelectChapter) LinearLayout tvSelectChapter;
    @InjectView(R.id.rlDrawer) View llDrawer;
    private ActionBarDrawerToggle mDrawerToggle;
    private String chapterName;
    private int chapterId;
    Fragment eventListFragment;
    FragmentManager fm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        getSupportActionBar().setTitle(chapterName);
        // annotation injection
        ButterKnife.inject(this);

        setupUi();
        Intent i = getIntent();
        chapterId = i.getIntExtra("ChapterId", -1);
        chapterName = i.getStringExtra("ChapterName");
        eventListFragment = HomeEventsFragment.newInstance(chapterName, chapterId);
        fm = getSupportFragmentManager();
        fm.beginTransaction().replace(R.id.flHomeContainer, eventListFragment).commit();
    }

    private void setupUi() {
        mDrawerToggle = setupDrawerToggle();
        dlDrawerLayout.setDrawerListener(mDrawerToggle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        tvMyEvents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeDrawer();
                final Intent intent = new Intent(HomeActivity.this, MyEventsActivity.class);
                HomeActivity.this.startActivity(intent);
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        setupLoginUi();
    }

    // change UI based on login status
    private void setupLoginUi() {

        final LoginManager loginManager = LoginManager.getInstance(this);
        if (loginManager.isLoggedIn()) {
            tvName.setVisibility(View.VISIBLE);
            tvName.setText(loginManager.getCurrentUser().getName());
            ivProfile.setVisibility(View.VISIBLE);
            ImageLoader imageLoader = ImageLoader.getInstance();
            imageLoader.displayImage(loginManager.getCurrentUser().getProfileImageUri(), ivProfile);
            tvMyEvents.setVisibility(View.VISIBLE);
            tvLogin.setVisibility(View.GONE);

        } else {
            tvName.setVisibility(View.GONE);
            ivProfile.setVisibility(View.GONE);
            tvMyEvents.setVisibility(View.GONE);
            tvLogin.setVisibility(View.VISIBLE);
            tvLogin.setText(R.string.login);
            tvLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    closeDrawer();
                    final Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                    HomeActivity.this.startActivity(intent);
                }
            });
        }

        tvSelectChapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getSupportFragmentManager();
                Fragment fragment = fm.findFragmentByTag(SELECT_CHAPTER_FRAGMENT_TAG);
                if (fragment != null) {
                    removeSelectChapterFragment();
                } else {
                    FragmentTransaction ft = fm.beginTransaction();
                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                    ft.add(R.id.flChaptersContainer,
                            SelectChapterFragment.newInstance(),
                            SELECT_CHAPTER_FRAGMENT_TAG);
                    ft.commit();
                }
            }
        });
    }

    private void closeDrawer() {
        dlDrawerLayout.closeDrawer(llDrawer);
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

    private void displayEventsInChapter(Chapter ch) {
        eventListFragment = HomeEventsFragment.newInstance(ch.getChapterName(),
                ch.getChapterId());
        fm = getSupportFragmentManager();
        fm.beginTransaction()
                .replace(R.id.flHomeContainer, eventListFragment)
                .commit();
        closeDrawer();

        getSupportActionBar().setTitle(ch.getChapterName());

        /*
        Saving the new chapter information if the user changes chapter
         */
        SharedPreferences.Editor editor = OneBrickApplication.getApplicationSharedPreference().edit();
        editor.putInt("MyChapterId", ch.getChapterId());
        editor.putString("MyChapterName", ch.getChapterName());
        editor.apply();

    }

    @Override
    public void onSelectChapter(@NonNull Chapter chapter) {
        displayEventsInChapter(chapter);
        removeSelectChapterFragment();
    }

    @Override
    public void onStartLoading() {
        pbSelectChapter.setVisibility(View.VISIBLE);
    }

    @Override
    public void onFinishLoading() {
        pbSelectChapter.setVisibility(View.GONE);
    }

    private void removeSelectChapterFragment() {
        fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentByTag(SELECT_CHAPTER_FRAGMENT_TAG);
        if (fragment != null) {
            FragmentTransaction ft = fm.beginTransaction();
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            ft.remove(fragment);
            ft.commit();
        }
    }

    private ActionBarDrawerToggle setupDrawerToggle() {
        return new ActionBarDrawerToggle(this, /* host Activity */
                dlDrawerLayout, /* DrawerLayout object */
                R.drawable.ic_navigation_drawer, /* nav drawer image to replace 'Up' caret */
                R.string.drawer_open, /* "open drawer" description for accessibility */
                R.string.drawer_close /* "close drawer" description for accessibility */
        ) {
            public void onDrawerClosed(View view) {
                removeSelectChapterFragment();
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


    public void startSearchEventsActivity (MenuItem mi) {
        Intent i = new Intent(getApplicationContext(), SearchActivity.class);
        i.putExtra("chapterId", ((HomeEventsFragment)eventListFragment).getChapterId());
        i.putExtra("chapterName", ((HomeEventsFragment)eventListFragment).getChapterName());
        startActivity(i);
        overridePendingTransition(R.anim.right_in, R.anim.left_out);
    }

    public void showCalendarView (MenuItem mi) {
        Intent i = new Intent(getApplicationContext(), CalendarViewActivity.class);
        i.putExtra("chapterId", ((HomeEventsFragment)eventListFragment).getChapterId());
        i.putExtra("chapterName", ((HomeEventsFragment)eventListFragment).getChapterName());
        startActivity(i);
        overridePendingTransition(R.anim.right_in, R.anim.left_out);
    }
}
