package org.onebrick.android.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBarActivity;

import org.onebrick.android.R;
import org.onebrick.android.core.OneBrickApplication;
import org.onebrick.android.fragments.SelectChapterFragment;
import org.onebrick.android.models.Chapter;

import butterknife.ButterKnife;

public class SelectChapterActivity extends ActionBarActivity implements
        SelectChapterFragment.OnSelectChapterFragmentListener {

    private static final String TAG = SelectChapterActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_chapter);
        ButterKnife.inject(this);
    }

    @Override
    public void onSelectChapter(@NonNull Chapter chapter) {
        OneBrickApplication.getInstance().setChapterName(chapter.getChapterName());
        OneBrickApplication.getInstance().setChapterId(chapter.getChapterId());

        Intent i = new Intent(getApplicationContext(), HomeActivity.class);
        i.putExtra("ChapterId", chapter.getChapterId());
        i.putExtra("ChapterName", chapter.getChapterName());
        startActivity(i);
        overridePendingTransition(R.anim.right_in, R.anim.left_out);
    }
}
