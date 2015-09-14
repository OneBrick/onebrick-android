package org.onebrick.android.activities;

import android.app.Dialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.squareup.otto.Subscribe;

import org.onebrick.android.R;
import org.onebrick.android.core.OneBrickApplication;
import org.onebrick.android.events.FetchEventsEvent;
import org.onebrick.android.events.Status;
import org.onebrick.android.fragments.HomeEventsFragment;
import org.onebrick.android.fragments.SearchResultsFragment;
import org.onebrick.android.fragments.SelectChapterFragment;
import org.onebrick.android.helpers.LoginManager;
import org.onebrick.android.models.Chapter;

import butterknife.ButterKnife;

public class HomeActivity extends ActionBarActivity
        implements SelectChapterFragment.OnSelectChapterListener, SearchView.OnQueryTextListener, MenuItemCompat.OnActionExpandListener {

    private static final String TAG = "HomeActivity";

    public static final String EXTRA_CHAPTER_ID = "chapter_id";
    public static final String EXTRA_CHAPTER_NAME = "chapter_name";

    Fragment eventListFragment;

    private Dialog mSelectChapterDialog;
    private SearchView mSearchView;
    private String mSearchQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.inject(this);

        Intent intent = getIntent();
        final int chapterId = intent.getIntExtra(EXTRA_CHAPTER_ID, -1);
        final String chapterName = intent.getStringExtra(EXTRA_CHAPTER_NAME);
        eventListFragment = HomeEventsFragment.newInstance(chapterName, chapterId);
        final FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction().replace(R.id.flHomeContainer, eventListFragment).commit();
        getSupportActionBar().setTitle(chapterName);

        // close fragment when back button is pressed
        fm.addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                if (getFragmentManager().getBackStackEntryCount() == 0) finish();
            }
        });

        OneBrickApplication.getInstance().getBus().register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        OneBrickApplication.getInstance().getBus().unregister(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        MenuItem searchItem = menu.findItem(R.id.mi_search);
        mSearchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        mSearchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        mSearchView.setOnQueryTextListener(this);
        MenuItemCompat.setOnActionExpandListener(searchItem, this);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.mi_search:
                return true;

            case R.id.mi_login:
                startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                return true;

            case R.id.mi_select_chapter: {
                // TODO this is holding reference for long time
                mSelectChapterDialog = new Dialog(this);
                mSelectChapterDialog.setContentView(R.layout.dialog_select_chapter);
                mSelectChapterDialog.setTitle(R.string.select_chapter);
                mSelectChapterDialog.show();
                return true;
            }

            case R.id.mi_my_events:
                startActivity(new Intent(HomeActivity.this, MyEventsActivity.class));
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
                return true;

            case R.id.mi_logout:
                logout();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        final LoginManager loginManager = LoginManager.getInstance(this);
        loginManager.logout();
        getSupportActionBar().invalidateOptionsMenu();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        final LoginManager loginManager = LoginManager.getInstance(this);
        if (loginManager.isLoggedIn()) {
            menu.findItem(R.id.mi_login).setVisible(false);
            menu.findItem(R.id.mi_logout).setVisible(true);
            menu.findItem(R.id.mi_my_events).setVisible(true);
        } else {
            menu.findItem(R.id.mi_login).setVisible(true);
            menu.findItem(R.id.mi_logout).setVisible(false);
            menu.findItem(R.id.mi_my_events).setVisible(false);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onSelectChapter(@NonNull Chapter chapter) {
        if (mSelectChapterDialog != null && mSelectChapterDialog.isShowing()) {
            mSelectChapterDialog.dismiss();
            mSelectChapterDialog = null;
        }
        OneBrickApplication.getInstance().setChapterName(chapter.getChapterName());
        OneBrickApplication.getInstance().setChapterId(chapter.getChapterId());
        displayEventsInChapter(chapter);
    }

    private void displayEventsInChapter(Chapter ch) {
        eventListFragment = HomeEventsFragment.newInstance(ch.getChapterName(), ch.getChapterId());
        final FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction().replace(R.id.flHomeContainer, eventListFragment).commit();
        getSupportActionBar().setTitle(ch.getChapterName());
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        mSearchQuery = query;
        displaySearchResults(mSearchQuery);
        // Reset SearchView
        mSearchView.clearFocus();
        mSearchView.setQuery(mSearchQuery, false);
        mSearchView.setIconified(false);
        return false;
    }

    private void displaySearchResults(String query) {
        int chapterId = OneBrickApplication.getInstance().getChapterId();
        String chapterName = OneBrickApplication.getInstance().getChapterName();
        eventListFragment = SearchResultsFragment.newInstance(chapterName, chapterId, query);
        final FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction().replace(R.id.flHomeContainer, eventListFragment).commit();
    }

    @Override
    public boolean onQueryTextChange(String query) {
        return false;
    }

    @Override
    public boolean onMenuItemActionExpand(MenuItem item) {
        return true;
    }

    @Override
    public boolean onMenuItemActionCollapse(MenuItem item) {
        // go back to initial status
        displaySearchResults("");
        return true;
    }

    @Subscribe
    public void onFetchEventsEvent(FetchEventsEvent event) {
        if (event.status == Status.NO_NETWORK) {
            Toast.makeText(this, R.string.no_network, Toast.LENGTH_LONG).show();
        } else if (event.status == Status.FAILED) {
            Toast.makeText(this, R.string.failed_to_fetch_chapters, Toast.LENGTH_LONG).show();
        }
    }
}
