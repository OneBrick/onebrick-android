package org.onebrick.android.models;

import android.database.Cursor;
import android.support.annotation.NonNull;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import org.onebrick.android.database.ChapterTable;

import java.lang.reflect.Type;

public class Chapter {
    private static final String TAG = "Chapter";

    private long _id;
    private int chapterId;
    private String name;

    @Override
    public String toString() {
        return name + " " + chapterId;
    }

    public long getID() {
        return _id;
    }

    public int getChapterId() {
        return chapterId;
    }

    public String getChapterName() {
        return name;
    }

    public static Chapter fromCursor(@NonNull Cursor cursor) {
        final Chapter ch = new Chapter();
        ch._id = cursor.getLong(cursor.getColumnIndexOrThrow(ChapterTable.Columns._ID));
        ch.chapterId = cursor.getInt(cursor.getColumnIndexOrThrow(ChapterTable.Columns.CHAPTER_ID));
        ch.name = cursor.getString(cursor.getColumnIndexOrThrow(ChapterTable.Columns.NAME));
        return ch;
    }

    public static class ChapterJsonDeserializer implements JsonDeserializer<Chapter> {
        @Override
        public Chapter deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            final JsonObject jsonObject = json.getAsJsonObject();
            final Chapter chapter = new Chapter();
            chapter.name = jsonObject.get("title").getAsString();
            chapter.chapterId = jsonObject.get("nid").getAsInt();
            return chapter;
        }
    }
}