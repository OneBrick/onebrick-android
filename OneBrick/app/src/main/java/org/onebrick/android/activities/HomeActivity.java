package org.onebrick.android.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import org.onebrick.android.helpers.LoginManager;
import org.onebrick.android.OneBrickApplication;
import org.onebrick.android.R;
import org.onebrick.android.fragments.HomeEventsFragment;
import org.onebrick.android.fragments.SelectChapterFragment;
import org.onebrick.android.models.Chapter;

public class HomeActivity extends FragmentActivity
        implements SelectChapterFragment.OnSelectChapterFragmentListener {

    private static final String TAG = HomeActivity.class.getName().toString();
    public static final String SELECT_CHAPTER_FRAGMENT_TAG = "select_chapter";

    private DrawerLayout dlDrawerLayout;
    private View llDrawer;
    private ActionBarDrawerToggle mDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        setupUi();
        Intent i = getIntent();
        int chapterId = i.getIntExtra("ChapterId", -1);
        String chapterName = i.getStringExtra("ChapterName");
        getActionBar().setTitle(chapterName);
        Fragment eventListFragment = HomeEventsFragment.newInstance(chapterName, chapterId);
        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction().replace(R.id.flHomeContainer, eventListFragment).commit();

    }

    private void setupUi() {
        dlDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        llDrawer = findViewById(R.id.rlDrawer);
        mDrawerToggle = setupDrawerToggle();
        dlDrawerLayout.setDrawerListener(mDrawerToggle);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        findViewById(R.id.tvMyEvents).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeDrawer();
                final Intent intent = new Intent(HomeActivity.this, MyEventsActivity.class);
                HomeActivity.this.startActivity(intent);
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
            final TextView tvName = (TextView)findViewById(R.id.tvName);
            tvName.setVisibility(View.VISIBLE);
            tvName.setText(loginManager.getCurrentUser().getName());

            findViewById(R.id.ivUserPic).setVisibility(View.VISIBLE);
            findViewById(R.id.tvMyEvents).setVisibility(View.VISIBLE);
            findViewById(R.id.tvLogin).setVisibility(View.GONE);

        } else {
            findViewById(R.id.tvName).setVisibility(View.GONE);
            findViewById(R.id.ivUserPic).setVisibility(View.GONE);
            findViewById(R.id.tvMyEvents).setVisibility(View.GONE);
            findViewById(R.id.tvLogin).setVisibility(View.VISIBLE);

            final TextView tvLogin = (TextView)findViewById(R.id.tvLogin);
            tvLogin.setText(R.string.login);
            tvLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    closeDrawer();
                    final Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                    HomeActivity.this.startActivity(intent);
                }
            });

//            findViewById(R.id.ibSetting).setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    // open setting activity
//                }
//            });
        }

        findViewById(R.id.tvSelectChapter).setOnClickListener(new View.OnClickListener() {
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

    private void displayEventsInChapter(Chapter ch) {
        Fragment eventListFragment = HomeEventsFragment.newInstance(ch.getChapterName(),
                ch.getChapterId());
        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction()
                .replace(R.id.flHomeContainer, eventListFragment)
                .commit();
        closeDrawer();

        getActionBar().setTitle(ch.getChapterName());

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

    private void removeSelectChapterFragment() {
        FragmentManager fm = getSupportFragmentManager();
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

}
