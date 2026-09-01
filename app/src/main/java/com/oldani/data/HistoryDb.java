package com.oldani.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.oldani.model.HistoryItem;
import java.util.ArrayList;
import java.util.List;

public class HistoryDb extends SQLiteOpenHelper {
    private static final String DB_NAME = "history.db";
    private static final int VERSION = 1;
    private static final String TABLE = "playback_history";
    private static HistoryDb instance;

    public static synchronized HistoryDb getInstance(Context ctx) {
        if (instance == null) instance = new HistoryDb(ctx.getApplicationContext());
        return instance;
    }

    private HistoryDb(Context ctx) { super(ctx, DB_NAME, null, VERSION); }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE + " ("
                + "episode_id INTEGER PRIMARY KEY,"
                + "position_millis INTEGER NOT NULL DEFAULT 0,"
                + "subject_id INTEGER,"
                + "subject_name TEXT,"
                + "subject_image_url TEXT,"
                + "episode_name TEXT,"
                + "duration_millis INTEGER DEFAULT 0,"
                + "updated_at_millis INTEGER NOT NULL DEFAULT 0)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldV, int newV) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE);
        onCreate(db);
    }

    public void save(int episodeId, long positionMillis, int subjectId, String subjectName,
                     String subjectImageUrl, String episodeName, long durationMillis) {
        ContentValues cv = new ContentValues();
        cv.put("episode_id", episodeId);
        cv.put("position_millis", positionMillis);
        cv.put("subject_id", subjectId);
        cv.put("subject_name", subjectName);
        cv.put("subject_image_url", subjectImageUrl);
        cv.put("episode_name", episodeName);
        cv.put("duration_millis", durationMillis);
        cv.put("updated_at_millis", System.currentTimeMillis());
        getWritableDatabase().replace(TABLE, null, cv);
    }

    public HistoryItem get(int episodeId) {
        Cursor c = getReadableDatabase().query(TABLE, null,
                "episode_id=?", new String[]{String.valueOf(episodeId)}, null, null, null);
        HistoryItem result = null;
        if (c.moveToFirst()) {
            result = cursorToItem(c);
        }
        c.close();
        return result;
    }

    public List<HistoryItem> getAll() {
        List<HistoryItem> list = new ArrayList<>();
        Cursor c = getReadableDatabase().query(TABLE, null, null, null, null, null,
                "updated_at_millis DESC");
        while (c.moveToNext()) {
            list.add(cursorToItem(c));
        }
        c.close();
        return list;
    }

    public void delete(int episodeId) {
        getWritableDatabase().delete(TABLE, "episode_id=?", new String[]{String.valueOf(episodeId)});
    }

    public void deleteAll() {
        getWritableDatabase().delete(TABLE, null, null);
    }

    private HistoryItem cursorToItem(Cursor c) {
        HistoryItem item = new HistoryItem();
        item.episodeId = c.getInt(c.getColumnIndexOrThrow("episode_id"));
        item.positionMillis = c.getLong(c.getColumnIndexOrThrow("position_millis"));
        item.subjectId = c.getInt(c.getColumnIndexOrThrow("subject_id"));
        item.subjectName = c.getString(c.getColumnIndexOrThrow("subject_name"));
        item.subjectImageUrl = c.getString(c.getColumnIndexOrThrow("subject_image_url"));
        item.episodeName = c.getString(c.getColumnIndexOrThrow("episode_name"));
        item.durationMillis = c.getLong(c.getColumnIndexOrThrow("duration_millis"));
        item.updatedAtMillis = c.getLong(c.getColumnIndexOrThrow("updated_at_millis"));
        return item;
    }
}