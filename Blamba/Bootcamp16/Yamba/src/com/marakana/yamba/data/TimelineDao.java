package com.marakana.yamba.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;


/**
 * TimelineDao
 */
public final class TimelineDao {
    /** Logging tag */
    public static final String TAG = "TimelineDao";

    static final int VERSION = 1;
    static final String DATABASE = "timeline.db";

    private static final String[] MAX_CREATED_AT_COLS= {
        "max(" + TimelineContract.Columns.CREATED_AT + ")"
    };

    private static final String[] TIMELINE_COLS = new String[] {
        BaseColumns._ID,
        TimelineContract.Columns.CREATED_AT,
        TimelineContract.Columns.USER,
        TimelineContract.Columns.TEXT
    };

    // DbHelper implementations
    private class DbHelper extends SQLiteOpenHelper {

        public DbHelper(Context context) {
            super(context, DATABASE, null, VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase sqlDb) {
            Log.i(TAG, "Creating database: " + DATABASE);
            sqlDb.execSQL(
                "create table " + TimelineContract.TABLE + " ("
                    + BaseColumns._ID + " int primary key, "
                    + TimelineContract.Columns.CREATED_AT + " int, "
                    + TimelineContract.Columns.USER + " text, "
                    + TimelineContract.Columns.TEXT + " text)");
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqlDb, int oldVersion, int newVersion) {
            sqlDb.execSQL("drop table " + TimelineContract.TABLE);
            onCreate(sqlDb);
        }
    }

    private final DbHelper dbHelper;
    private SQLiteDatabase db;

    /**
     * Creating the DB helper will not create the DB.
     *
     * @param context
     */
    public TimelineDao(Context context) {
        dbHelper = new DbHelper(context);
        Log.i(TAG, "Initialized data");
    }

    /**
     * @return the time of the most recent status message in the db
     */
    public long getLatestStatusCreatedAtTime() {
        Cursor cursor = getDb().query(
            TimelineContract.TABLE,
            MAX_CREATED_AT_COLS,
            null,
            null,
            null,
            null,
            null);

        try { return cursor.moveToNext() ? cursor.getLong(0) : Long.MIN_VALUE; }
        finally { cursor.close(); }
    }

    /**
     * @param values
     * @return true iff row was inserted
     */
    public boolean insertOrIgnore(ContentValues values) {
        try { return 0 < getDb().insertOrThrow(TimelineContract.TABLE, null, values); }
        catch (SQLException e) { }
        return false;
    }

    /**
     * @return a cursor to the timeline
     */
    public Cursor getTimeline() {
        return getDb().query(
            TimelineContract.TABLE,
            TIMELINE_COLS,
            null,
            null,
            null,
            null,
            TimelineContract.Columns.DEFAULT_SORT_ORDER);
    }

    /**
     * @param projection
     * @param sel
     * @param args
     * @param sort
     * @return a Cursor to the query results
     */
    public Cursor query(String[] projection, String sel, String[] args, String sort) {
        return getDb().query(
            TimelineContract.TABLE,
            projection,
            sel,
            args,
            null,
            null,
            sort);
    }

    private synchronized SQLiteDatabase getDb() {
        if (null == db) { db = dbHelper.getWritableDatabase(); }
        return db;
    }
}
