package org.onebrick.android.activities;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import org.onebrick.android.R;
import org.onebrick.android.core.OneBrickApplication;
import org.onebrick.android.fragments.HomeEventsFragment;
import org.onebrick.android.fragments.SelectChapterFragment;
import org.onebrick.android.helpers.LoginManager;
import org.onebrick.android.models.Chapter;

import butterknife.ButterKnife;

public class HomeActivity extends ActionBarActivity
        implements SelectChapterFragment.OnSelectChapterListener {

    private static final String TAG = HomeActivity.class.getName();

    public static final String EXTRA_CHAPTER_ID = "chapter_id";
    public static final String EXTRA_CHAPTER_NAME = "chapter_name";

    Fragment eventListFragment;

    private Dialog mSelectChapterDialog;

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
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.mi_search:
                startSearchEventsActivity(item);
                return true;

            case R.id.mi_login:
                startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                return true;

            case R.id.mi_select_chapter: {
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
                return true;
        }

        return super.onOptionsItemSelected(item);
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

    private void displayEventsInChapter(Chapter ch) {
        eventListFragment = HomeEventsFragment.newInstance(ch.getChapterName(),
                ch.getChapterId());
        final FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction()
                .replace(R.id.flHomeContainer, eventListFragment)
                .commit();

        getSupportActionBar().setTitle(ch.getChapterName());
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

    private void startSearchEventsActivity (MenuItem mi) {
        Intent i = new Intent(getApplicationContext(), SearchActivity.class);
        i.putExtra(SearchActivity.EXTRA_CHAPTER_ID, ((HomeEventsFragment)eventListFragment).getChapterId());
        i.putExtra(SearchActivity.EXTRA_CHAPTER_NAME, ((HomeEventsFragment)eventListFragment).getChapterName());
        startActivity(i);
        overridePendingTransition(R.anim.right_in, R.anim.left_out);
    }
}
