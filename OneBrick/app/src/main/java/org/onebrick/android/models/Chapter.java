package org.onebrick.android.models;

/**
 * Created by AshwinGV on 10/11/14.
 */

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

@Table(name="Chapters")
public class Chapter extends Model {
    @Column(name="Name",
            notNull = true, unique=true,
            onUniqueConflict = Column.ConflictAction.REPLACE)
    String name;

    @Column(name="ChapterId",
            notNull = true, unique=true,
            onUniqueConflict = Column.ConflictAction.REPLACE)
    int id;


    public Chapter(){
        super();
    }


    public Chapter(String name, int id) {
        super();
        this.name = name;
        this.id = id;
    }

    public String toString() {
        return ""+this.name+" "+this.id;
    }

    public String getChapterName() {
        return this.name;
    }

    public int getChapterId() {
        return this.id;
    }

    public String getChaterIdAsString() {
        return ""+this.id;
    }

    public static Chapter getChapterFromJsonObject(JSONObject jsonObject) {
        Chapter newChapter = new Chapter();
        try {
            newChapter.name = jsonObject.getString("title");
            newChapter.id = jsonObject.getInt("nid");
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
                Chapter toAdd = getChapterFromJsonObject(chapterJsonObject);
                if(toAdd != null) {
                    chapterList.add(toAdd);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return chapterList;
    }

}