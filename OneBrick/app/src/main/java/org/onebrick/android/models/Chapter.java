package org.onebrick.android.models;

import android.database.Cursor;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;
import android.util.Log;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.From;
import com.activeandroid.query.Select;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

@Table(name = "chapters", id = BaseColumns._ID)
public class Chapter extends Model {

    private static final String TAG = Chapter.class.getName().toString();

    @Column(name="name",
            notNull = true, unique=true,
            onUniqueConflict = Column.ConflictAction.REPLACE)
    String name;

    @Column(name="chapter_id",
            notNull = true, unique=true,
            onUniqueConflict = Column.ConflictAction.REPLACE)
    int chapterId;

    @Override
    public String toString() {
        return name + " " + chapterId;
    }

    public String getChapterName() {
        return this.name;
    }

    public int getChapterId() {
        return this.chapterId;
    }

    public static Chapter getChapterFromJsonObject(JSONObject jsonObject) {
        Chapter newChapter = new Chapter();
        try {
            newChapter.name = jsonObject.getString("title");
            newChapter.chapterId = jsonObject.getInt("nid");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.i(TAG,"Saving Chapter to DB");
        newChapter.save();
        return newChapter;
    }

    public static ArrayList<Chapter> getChapterListFromJsonObject(JSONObject jsonObject) {
        ArrayList<Chapter> chapterList = new ArrayList<Chapter>();
        Iterator<String> iter = jsonObject.keys();
        while (iter.hasNext()) {
            String key = iter.next();
            try {
                JSONObject chapterJsonObject = jsonObject.getJSONObject(key);
                //Log.i("PASS",""+object.toString());
                Chapter toAdd = findOrCreateFromJson(chapterJsonObject);
                if(toAdd != null) {
                    chapterList.add(toAdd);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (!chapterList.isEmpty()) {
            Collections.sort(chapterList, new Comparator<Chapter>(){
                @Override
                public int compare(Chapter ch1, Chapter ch2) {
                    return ch1.getChapterName().compareTo(ch2.getChapterName());
                }
            });
        }
        return chapterList;
    }

    // Finds existing user based on remoteId or creates new user and returns
    public static Chapter findOrCreateFromJson(JSONObject jsonObj) {
        int eventId = jsonObj.optInt("nid");
        Chapter existingChapter =
                new Select().from(Chapter.class).where("ChapterId = ?", eventId).executeSingle();
        if (existingChapter != null) {
            // found and return existing
            Log.i(TAG, "Returning existing chapter. Not saving new chapter to DB");
            return existingChapter;
        } else {
            // create and return new
            Chapter chapter = getChapterFromJsonObject(jsonObj);
            return chapter;
        }
    }

    public static Chapter getChapterFromId(int id) {
        return new Select().from(Chapter.class)
                .where("ChapterId = ?", id)
                .executeSingle();
    }

    public static List<Chapter> getChapterListFromDb() {
        Log.i(TAG,"Getting Chapters from DB");
        From sql = new Select().from(Chapter.class);
        Log.i("SQL is", sql.toSql());
        return sql.execute();
    }

    public static Chapter fromCursor(@NonNull Cursor cursor) {
        final Chapter ch = new Chapter();
        ch.loadFromCursor(cursor);
        return ch;
    }
}