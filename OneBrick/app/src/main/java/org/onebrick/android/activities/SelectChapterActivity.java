package org.onebrick.android.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.squareup.otto.Subscribe;

import org.onebrick.android.R;
import org.onebrick.android.core.OneBrickApplication;
import org.onebrick.android.events.FetchChaptersEvent;
import org.onebrick.android.events.Status;
import org.onebrick.android.fragments.SelectChapterFragment;
import org.onebrick.android.models.Chapter;

import butterknife.ButterKnife;

public class SelectChapterActivity extends AppCompatActivity implements
        SelectChapterFragment.OnSelectChapterListener {

    private static final String TAG = "SelectChapterActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_chapter);
        ButterKnife.bind(this);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.select_chapter_fragment, new SelectChapterFragment());
        ft.commit();

        OneBrickApplication.getInstance().getBus().register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        OneBrickApplication.getInstance().getBus().unregister(this);
    }

    @Override
    public void onSelectChapter(@NonNull Chapter chapter) {
        OneBrickApplication.getInstance().setChapterName(chapter.getChapterName());
        OneBrickApplication.getInstance().setChapterId(chapter.getChapterId());

        final Intent intent = new Intent(this, HomeActivity.class);
        intent.putExtra(HomeActivity.EXTRA_CHAPTER_ID, chapter.getChapterId());
        intent.putExtra(HomeActivity.EXTRA_CHAPTER_NAME, chapter.getChapterName());
        startActivity(intent);
        overridePendingTransition(R.anim.right_in, R.anim.left_out);
    }

    @Subscribe
    public void onFetchChaptersEvent(FetchChaptersEvent event) {
        if (event.status == Status.NO_NETWORK) {
            Toast.makeText(this, R.string.no_network, Toast.LENGTH_LONG).show();
        } else if (event.status == Status.FAILED) {
            Toast.makeText(this, R.string.failed_to_fetch_chapters, Toast.LENGTH_LONG).show();
        }
    }
}
