package org.onebrick.android.models;

import android.database.Cursor;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

@Table(name = "chapter", id = BaseColumns._ID)
public class Chapter extends Model {
    private static final String TAG = "Chapter";
    public static final String NAME = "name";
    public static final String CHAPTER_ID = "chapter_id";

    @Column(name = CHAPTER_ID, unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    private int chapterId;

    @Column(name = NAME)
    private String name;

    @Override
    public String toString() {
        return name + " " + chapterId;
    }

    public int getChapterId() {
        return chapterId;
    }

    public String getChapterName() {
        return name;
    }

    public static Chapter fromCursor(@NonNull Cursor cursor) {
        final Chapter ch = new Chapter();
        ch.loadFromCursor(cursor);
        return ch;
    }

    @Nullable
    public static Chapter findById(long chapterId) {
        return new Select().from(Chapter.class).where("chapter_id=?",
                chapterId).executeSingle();
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