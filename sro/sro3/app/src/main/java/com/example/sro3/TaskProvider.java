package com.example.sro3;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class TaskProvider extends ContentProvider {
    private DBHelper db;
    private static final String AUTH = "com.example.sro3.provider";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTH + "/tasks");
    private static final UriMatcher UM = new UriMatcher(UriMatcher.NO_MATCH);
    static { UM.addURI(AUTH, "tasks", 1); UM.addURI(AUTH, "tasks/#", 2); }

    @Override public boolean onCreate() { db = new DBHelper(getContext()); return true; }
    @Nullable @Override public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase d = db.getReadableDatabase();
        return UM.match(uri) == 2 ? d.query(TaskContract.TaskEntry.TABLE, projection, TaskContract.TaskEntry._ID + "=?", new String[]{uri.getLastPathSegment()}, null, null, sortOrder)
                : d.query(TaskContract.TaskEntry.TABLE, projection, selection, selectionArgs, null, null, sortOrder);
    }
    @Nullable @Override public Uri insert(@NonNull Uri uri, ContentValues values) {
        long id = db.getWritableDatabase().insert(TaskContract.TaskEntry.TABLE, null, values);
        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, id);
    }
    @Override public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        int count = UM.match(uri) == 2 ? db.getWritableDatabase().delete(TaskContract.TaskEntry.TABLE, TaskContract.TaskEntry._ID + "=?", new String[]{uri.getLastPathSegment()})
                : db.getWritableDatabase().delete(TaskContract.TaskEntry.TABLE, selection, selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }
    @Override public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) { return 0; }
    @Nullable @Override public String getType(@NonNull Uri uri) { return null; }
}
