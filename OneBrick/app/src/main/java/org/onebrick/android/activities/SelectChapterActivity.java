package org.onebrick.android.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONObject;
import org.onebrick.android.OneBrickApplication;
import org.onebrick.android.OneBrickClient;
import org.onebrick.android.R;
import org.onebrick.android.adapters.NavigationChapterListAdapter;
import org.onebrick.android.models.Chapter;

import java.util.ArrayList;

public class SelectChapterActivity extends Activity {

    ListView lvChapters;
    NavigationChapterListAdapter chapterListAdapter;
    ArrayList<Chapter> chapterList;
    OneBrickClient obClient;
    private static final String TAG = HomeActivity.class.getName().toString();

    JsonHttpResponseHandler chapterListResponseHandler = new JsonHttpResponseHandler() {
        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            chapterList.clear();
            chapterList.addAll(Chapter.getChapterListFromJsonObject(response));
            chapterListAdapter.addAll(chapterList);
            chapterListAdapter.notifyDataSetChanged();
            Log.i(TAG, "" + chapterList);
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
            Log.i(TAG,"Api called failed!");
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_chapter);
        lvChapters = (ListView) findViewById(R.id.lvChapterPrompt);
        chapterList = new ArrayList<Chapter>();
        obClient = OneBrickApplication.getRestClient();
        chapterListAdapter = new NavigationChapterListAdapter(this,R.layout.drawer_nav_item,chapterList);
        lvChapters.setAdapter(chapterListAdapter);
        obClient.getChapters(chapterListResponseHandler);
        lvChapters.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Chapter ch = chapterListAdapter.getItem(position);
                Intent i = new Intent(getApplicationContext(), HomeActivity.class);
                i.putExtra("ChapterId",ch.getChapterId());
                i.putExtra("ChapterName", ch.getChapterName());
                startActivity(i);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.select_chapter, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
