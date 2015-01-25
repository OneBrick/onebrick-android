package org.onebrick.android.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import org.onebrick.android.R;
import org.onebrick.android.helpers.Utils;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class EventDescription extends ActionBarActivity {
    @InjectView(R.id.tvEventDescription) TextView tvEventDetails;
    String details;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_description);
        getSupportActionBar().setTitle("Event Description");
        // annotation injection
        ButterKnife.inject(this);
        Intent eventInfo = getIntent();
        details = eventInfo.getStringExtra("Details");
        tvEventDetails.setText(Html.fromHtml(Utils.removeImgTagsFromHTML(details)));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.event_description, menu);
        return true;
    }
    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.left_in, R.anim.right_out);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
