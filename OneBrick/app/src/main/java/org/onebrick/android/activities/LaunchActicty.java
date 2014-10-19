package org.onebrick.android.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;

import org.onebrick.android.OneBrickApplication;
import org.onebrick.android.R;

public class LaunchActicty extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch_acticty);
        getActionBar().hide();
        final String  myChapterName = OneBrickApplication
                .getApplicationSharedPreference()
                .getString("MyChapterName", null);
        final int myChapterId = OneBrickApplication
                .getApplicationSharedPreference()
                .getInt("MyChapterId", -1);
        if(myChapterName == null) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    Intent i = new Intent(LaunchActicty.this, SelectChapterActivity.class);
                    startActivity(i);
                }
            }, 2500);
        } else {
            //Toast.makeText(this,"My Chapter is "+myChapterName,Toast.LENGTH_LONG).show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    Intent i = new Intent(getApplicationContext(), HomeActivity.class);
                    i.putExtra("ChapterId",myChapterId);
                    i.putExtra("ChapterName", myChapterName);
                    startActivity(i);
                }
            }, 2500);

        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.launch_acticty, menu);
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
